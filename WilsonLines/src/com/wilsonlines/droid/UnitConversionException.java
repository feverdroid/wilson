package com.wilsonlines.droid;


/**
 * Exception thrown when a mesaure unit conversion cannot take place.
 * @author unnamed
 *
 */
public class UnitConversionException extends Exception {

	private static final long serialVersionUID = 841409567565526654L;

	public UnitConversionException() { super(); }

	public UnitConversionException(String detailMessage) { super(detailMessage); }

	public UnitConversionException(Throwable throwable) { super(throwable); }

	public UnitConversionException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
}
