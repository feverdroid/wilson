package com.wilsonlines.droid;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Builds converters based on the provided conversion data
 * @author Pedro Velasco
 */
public class UnitConverterBuilder {

	/**
	 * The conversion factors are stored in a map of maps to be searched
	 * by the destination measure unit such as:
	 *  to -> from -> rate.
	 */
	Map<String, Map<String, Double>> conversions =
		new HashMap<String, Map<String,Double>>();

	/**
	 * Load conversion data from a JSONArray.
	 * @param data the new conversion data.
	 * @throws JSONException if the JSON data is malformed or cannot be handled.
	 */
	public void setConversionData(JSONArray data) throws JSONException {

		final Map<String, Map<String, Double>> conversions =
				new HashMap<String, Map<String,Double>>();

		//Insert each dictionary in the conversions map
		for (int i = 0; i < data.length(); i++) {
			final JSONObject json = data.getJSONObject(i);
			final String from = json.getString("from");
			final String to = json.getString("to");
			final Double rate = json.getDouble("rate");

			//Get the Map with the rates for the "to" measure unit.
			Map<String, Double> rates = conversions.get(to);
			if (null == rates) {
				rates = new HashMap<String, Double>();
				conversions.put(to, rates);
			}

			rates.put(from, rate);
		}

		//If everything went well, save the new Map
		this.conversions = conversions;
	}

	/**
	 * Generates a new {@link UnitConverter} from the current conversion data.
	 * @param to measure unit for the new converter
	 * @return The converter to convert into the desired measure unit.
	 */
	public UnitConverter build(String to) {

		/** Save the to for a detail exception message */
		final String toAux = to;

		/** Computed rates for the desired measure */
		final Map<String, Double> rates = this.generateRates(to);

		final UnitConverter converter = new UnitConverter() {
			@Override
			public double convert(String from, double value)
				throws UnitConversionException {

				final Double rate = rates.get(from);

				if (null == rate) {
					throw new UnitConversionException(
						"No available conversion from " + from +
						" to " + toAux);
				}

				return value * rate;
			}
		};

		return converter;
	}

	/**
	 * Generates a Map of conversion rates for a given measure unit.
	 * The map can always convert at least to itself.
	 * @param to measure unit to compure rates for
	 * @return a map with the conversion rates from available measure
	 *  units to this unit.
	 */
	private Map<String, Double> generateRates(String to) {
		/** Pending measures to be scanned in the graph */
		final Queue<String> measures = new PriorityQueue<String>();

		/** Computed rates for the desired measure */
		final Map<String, Double> rates = new HashMap<String, Double>();

		//Initialise output data with the identity conversion
		// (to -> to -> 1.0)
		rates.put(to, 1.0);
		measures.offer(to);

		//while we have nodes to follow in the graph
		while (!measures.isEmpty()) {
			final String measure = measures.remove();
			final double measureRate = rates.get(measure);

			//Ger available conversions to this measure
			Map<String, Double> fromMap = this.conversions.get(measure);
			if (null == fromMap) {
				continue;
			}

			//For each conversion rate, if we don't already
			// have it, compute and insert it in the rates map
			// and in the measure units queue
			for (String from : fromMap.keySet()) {
				if (!rates.containsKey(from)) {
					rates.put(from, fromMap.get(from) * measureRate);
					measures.offer(from);
				}
			}
		}

		return rates;
	}
}
