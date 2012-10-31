package com.wilsonlines.droid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.app.Activity;
import android.content.Intent;

/**
 * Downloads the data and shows the list of countries.
 * @author Pedro Velasco
 *
 */
public class CountriesActivity extends Activity {
	private final static String URI_CONVERSIONS = "http://app.feverup.com/3as3224jksi2342/conversions.json";
	private final static String URI_PACKAGES = "http://app.feverup.com/3as3224jksi2342/packages.json";

	private List<Country> countries;
	private ArrayAdapter<Country> countriesAdapter;
	
	private AsyncTask<Void, Void, List<Country>> downloaderTask = null;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.countries);

		this.countries = (List<Country>) this.getLastNonConfigurationInstance();

		if (null == this.countries) {
			this.countries = new ArrayList<Country>();
		}

		/* Setup list adapter */
		this.countriesAdapter = new ArrayAdapter<Country>(this, android.R.layout.simple_list_item_1, this.countries);
		final ListView list = (ListView) this.findViewById(R.id.countries_list);
		list.setAdapter(countriesAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				/* Start the detail activity with the selected country */
				Country selected = (Country) parent.getAdapter().getItem(position);
				AppSingleton.getInstance().setCountry(selected);

				Intent intent = new Intent(CountriesActivity.this, PackagesActivity.class);
				CountriesActivity.this.startActivity(intent);
			}
		});

		/* Setup reload button */
		final Button button = (Button) this.findViewById(R.id.countries_reload);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CountriesActivity.this.reloadData();
			}
		});
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return this.countries;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != this.downloaderTask) {
			this.downloaderTask.cancel(true);
		}
	}

	/**
	 * Downloads the data from the network and updates the user interface.
	 */
	public void reloadData() {

		/* Start a new download only if the previous one has already finished */
		if (null == this.downloaderTask || AsyncTask.Status.FINISHED == this.downloaderTask.getStatus()) {
			this.downloaderTask = new AsyncTask<Void, Void, List<Country>>() {

				@Override
				protected List<Country> doInBackground(Void... params) {
					/* We use a map because it's easier to search by key (country code) */
					final HashMap<String, Country> countriesMap = new HashMap<String, Country>();

					final String conversionsString = this.downloadJsonData(URI_CONVERSIONS);
					/* Do not continue if cancelled */
					if (this.isCancelled()) {
						return null;
					}

					final String packagesString = this.downloadJsonData(URI_PACKAGES);
					/* Do not continue if cancelled */
					if (this.isCancelled()) {
						return null;
					}

					/* Extract package information from the JSON */
					try {
						final UnitConverterBuilder builder = new UnitConverterBuilder();
						builder.setConversionData(new JSONArray(conversionsString));
						final JSONArray data = new JSONArray(packagesString);
						final List<Package> packages = Package.parse(data, builder);

						/* Create the list of countries */
						for (Package p : packages) {
							Country c = countriesMap.get(p.getDestination());
							if (null == c) {
								c = new Country(p.getDestination());
								countriesMap.put(c.getId(), c);
							}
							c.addPackage(p);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (UnitConversionException e) {
						e.printStackTrace();
					}

					/* Retrieve and sort the countries */
					final List<Country> countries = new ArrayList<Country>();
					countries.addAll(countriesMap.values());
					Collections.sort(countries);

					return countries;
				}

				@Override
				protected void onPostExecute(List<Country> countries) {
					CountriesActivity.this.countries.clear();
					CountriesActivity.this.countries.addAll(countries);
					CountriesActivity.this.countriesAdapter.notifyDataSetChanged();
				}

				/**
				 * Downloads a JSON file from somewhere
				 * @param uri to get the file from
				 * @return the data of the JSON file
				 */
				private String downloadJsonData(String uri) {
					StringBuilder builder = new StringBuilder();
					URL url;
					HttpURLConnection urlConnection;

					/* Disable connection pool if using Froyo or earlier */
					if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
						System.setProperty("http.keepAlive", "false");
					}

					try {
						url = new URL(uri);
						urlConnection = (HttpURLConnection) url.openConnection();
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return null;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return null;
					}
					
					try {
						InputStream content = urlConnection.getInputStream();
						final int httpStatus = urlConnection.getResponseCode();

						if (200 == httpStatus) {
							BufferedReader reader = new BufferedReader(new InputStreamReader(content));
							String line;
							while ((line = reader.readLine()) != null) {
								builder.append(line);
								/* Periodic check for cancellation */
								if (this.isCancelled()) {
									return null;
								}
							}
						} else {
							Log.e(CountriesActivity.class.toString(), "Failed to download file:" + uri);
						}
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						urlConnection.disconnect();
					}
					return builder.toString();
				}

			};
			this.downloaderTask.execute((Void)null);
		}
	}
}
