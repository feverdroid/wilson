package com.wilsonlines.droid;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages country data
 * @author Pedro Velasco
 */
public class Country implements Comparable<Country> {
	private static final double MAX_KG_PER_PLANE = 2000;

	private final String id;
	private final List<Package> packages;

	/**
	 * Constructs a new country with an empty package list
	 * @param id The Country Code
	 */
	public Country(String id) {
		this.id = id;
		this.packages = new ArrayList<Package>();
	}

	@Override
	public String toString() {
		return this.id;
	}

	@Override
	public boolean equals(Object o){
		return (o instanceof Country && this.id.equals(((Country) o).id));
	}

	@Override
	public int compareTo(Country c) {
		return this.id.compareTo(c.id);
	}

	/**
	 * Add a new package for this country
	 * @param pack
	 */
	public void addPackage(Package pack) {
		this.packages.add(pack);
	}

	/**
	 * @return the id (name) of this country
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the current package list
	 */
	public List<Package> getPackages() {
		return new ArrayList<Package>(this.packages);
	}

	/**
	 * Compute the number of needed airplanes for this country, based
	 *  on the current packages to deliver.
	 * @return the number of needed airplanes.
	 */
	public int neededAirplanes() {
		double weight = this.totalKg();
		int planes = (int)(weight / MAX_KG_PER_PLANE);

		if (planes * MAX_KG_PER_PLANE < weight) {
			planes++;
		}

		return planes;
	}

	/**
	 * Compute the total weight for this country
	 * @return the total amount of weight
	 */
	public double totalKg() {
		double total = 0;
		for (Package p : this.packages) {
			total += p.getKg();
		}

		return total;
	}
}
