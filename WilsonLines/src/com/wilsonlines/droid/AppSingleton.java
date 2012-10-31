package com.wilsonlines.droid;


/**
 * Singleton to share data between activities
 * @author Pedro Velasco
 */
public class AppSingleton {
	private static AppSingleton singleton = null;

	private Country country;

	/**
	 * Gets this singleton instance
	 * @return the singleton
	 */
	public synchronized static AppSingleton getInstance() {
		if (null == singleton) {
			singleton = new AppSingleton();
		}
		return singleton;
	}

	public Country getCountry() {
		return this.country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

}
