package com.wilsonlines.droid;

import java.util.Collections;
import java.util.List;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Shows the packages and details of a country
 * @author Pedro Velasco
 */
public class PackagesActivity extends Activity {

	private Country country;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.packages);

		this.country = (Country) this.getLastNonConfigurationInstance();

		if (null == this.country) {
			this.country = AppSingleton.getInstance().getCountry();
		}
		
		/* Setup the country code, airplanes and weight */
		final Resources resources = this.getResources();
		final String countryCode = country.toString();
		final int airplanes = country.neededAirplanes();
		final double weight = country.totalKg(); 

		TextView textView = (TextView) this.findViewById(R.id.packages_countryCode);
		textView.setText(countryCode);

		textView = (TextView) this.findViewById(R.id.packages_airplanes);
		textView.setText(resources.getQuantityString(R.plurals.packages_airplanes, airplanes, airplanes));

		textView = (TextView) this.findViewById(R.id.packages_weight);
		String weightString = String.format(resources.getString(R.string.packages_weight), weight);
		textView.setText(weightString);

		/* Setup the list adapter */
		final List<Package> packages = this.country.getPackages();
		Collections.sort(packages);
		final ArrayAdapter<Package> packagesAdapter =
			new ArrayAdapter<Package>(this, android.R.layout.simple_list_item_1, packages);

		final ListView list = (ListView) findViewById(R.id.packages_list);
		list.setAdapter(packagesAdapter);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return this.country;
	}
}
