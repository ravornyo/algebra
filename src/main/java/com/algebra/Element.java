package com.algebra;

public class Element implements IElement {
	
	private double value;

	public Element() {
		this(0.0);
	}
	
	public Element(double value) {
		setValue(value);
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public void add(double value) {
		this.value +=  value;
	}

	@Override
	public void subtract(double value) {
		this.value -=  value;
	}

}
