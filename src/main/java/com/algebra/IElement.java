package com.algebra;

/**
 * A description of a matrix element.
 * 
 * @author avornyo
 *
 * @param <T> The base type
 */
public interface IElement {
	/**
	 * Gets the value of the element
	 * 
	 * @return The value of the element
	 */
	public double getValue();
	/**
	 * Sets the value of the element
	 * 
	 * @param value The value of the element
	 */
	public void setValue(double value);
	
	public void add(double value);
	
	public void subtract(double value);
}