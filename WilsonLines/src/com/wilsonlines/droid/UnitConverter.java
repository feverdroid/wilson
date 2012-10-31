package com.wilsonlines.droid;

/**
 * A unit converter is able to convert from an arbitrary
 *  range of measure units into the measure unit of the converter.
 *
 * @author Pedro Velasco
 */
public interface UnitConverter {

	/**
	 * Converts a measure unit into the native measure unit of the converter.
	 * @param from what measure unit to convert.
	 * @param value to convert.
	 * @return the value as measured in the measure unit of the converter.
	 * @throws UnitConversionException if the source measure unit is not
	 *  available for the converter.
	 */
	public double convert(String from, double value)
		throws UnitConversionException;
}
