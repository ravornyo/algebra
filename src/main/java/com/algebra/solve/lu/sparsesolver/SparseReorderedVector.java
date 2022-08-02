package com.algebra.solve.lu.sparsesolver;

import com.algebra.vector.IVector;
import com.algebra.vector.sparsevector.ISparseVector;
import com.algebra.vector.sparsevector.ISparseVectorElement;

public class SparseReorderedVector implements ISparseVector {

	private final SparseLUSolver _parent;
	
	public SparseReorderedVector(SparseLUSolver parent) {
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

	@Override
	public int getElementCount() {
		return this._parent.getVector().getElementCount();
	}

	@Override
	public ISparseVectorElement getFirstInVector() {
		return this._parent.getVector().getFirstInVector();
	}

	@Override
	public ISparseVectorElement getLastInVector() {
		return this._parent.getVector().getLastInVector();
	}

	@Override
	public ISparseVectorElement getElement(int index) {
		return this._parent.getVector().getElement(index);
	}

	@Override
	public boolean removeElement(int index) {
		return this._parent.getVector().removeElement(index);
	}

	@Override
	public ISparseVectorElement findElement(int index) {
		return this._parent.getVector().findElement(index);
	}

}
