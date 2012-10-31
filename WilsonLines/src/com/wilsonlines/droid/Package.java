package com.wilsonlines.droid;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Manages package data
 * @author Pedro Velasco
 */
public class Package implements Comparable<Package> {

	/** Id of this package */
	private final String id;

	/** weight in Kg of this package */
	private final double weight;

	/** Destination country code for this package */
	private String destination;

	/**
	 * Generates a list of packages from the JSON data.
	 * @param data with the JSON array to parse.
	 * @param builder to get a UnitConverter.
	 * @return the list of package objects.
	 * @throws JSONException if the data is malformed or cannot be handled.
	 * @throws UnitConversionException if the measure of any packet cannot
	 *  be handled.
	 */
	public static List<Package> parse(JSONArray data, UnitConverterBuilder builder)
			throws JSONException, UnitConversionException {

		List<Package> packages = new ArrayList<Package>();
		UnitConverter converter = builder.build("KG");

		for (int i = 0; i < data.length(); i++) {
			packages.add(new Package(data.getJSONObject(i), converter));
		}

		return packages;
	}

	/**
	 * Create a new package from the JSON data.
	 * @throws JSONException if the data is malformed or cannot be handled.
	 * @throws UnitConversionException if the measure of any packet cannot
	 *  be handled.
	 */
	private Package(JSONObject packet, UnitConverter converter)
			throws JSONException, UnitConversionException {
		this.id = packet.getString("id");
		this.destination = packet.getString("destination");
		this.weight = converter.convert(packet.getString("measure"),
						packet.getDouble("weight"));
	}

	@Override
	public String toString() {
		return this.id;
	}

	@Override
	public boolean equals(Object o){
		return (o instanceof Package && this.id.equals(((Package) o).id));
	}

	@Override
	public int compareTo(Package p) {
		return this.id.compareTo(p.id);
	}

	/**
	 * @return the id of this package
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the destination country code of this package
	 */
	public String getDestination() {
		return this.destination;
	}

	/**
	 * @return the weight of this package in Kg.
	 */
	public double getKg() {
		return this.weight;
	}
}
