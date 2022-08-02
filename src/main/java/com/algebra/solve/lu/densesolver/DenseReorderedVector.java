package com.algebra.solve.lu.densesolver;

import com.algebra.vector.IVector;

public class DenseReorderedVector implements IVector {

	private final DenseLUSolver _parent;
	
	public DenseReorderedVector(DenseLUSolver parent) {
		this._parent = parent;
	}

	@Override
	public int length() {
		return this._parent.getVector().length();
	}

	@Override
	public double get(int index) {
		return this._parent.getVector().get(index);
	}

	@Override
	public void set(int index, double value) {
		this._parent.getVector().set(index, value);
	}

	@Override
	public void swapElements(int index1, int index2) {
		this._parent.getVector().swapElements(index1, index2);
	}

	@Override
	public void copyTo(IVector target) {
		this._parent.getVector().copyTo(target);
	}

	@Override
	public void reset() {
		this._parent.getVector().reset();
	}

	@Override
	public void clear() {
		this._parent.getVector().clear();
	}

}
