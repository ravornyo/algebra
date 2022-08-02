package com.algebra.solve.lu.densesolver;

import com.algebra.matrix.IMatrix;
import com.algebra.matrix.MatrixLocation;

public class DenseReorderedMatrix implements IMatrix{

	private final DenseLUSolver _parent;
	
	public DenseReorderedMatrix(DenseLUSolver parent) {
		this._parent = parent;
	}

	@Override
	public int getSize() {
		return this._parent.getMatrix().getSize();
	}

	@Override
	public double get(int row, int column) {
		return this._parent.getMatrix().get(row, column);
	}

	@Override
	public void set(int row, int column, double value) {
		this._parent.getMatrix().set(row, column, value);
	}

	@Override
	public double get(MatrixLocation location) {
		return this._parent.getMatrix().get(location);
	}

	@Override
	public void set(MatrixLocation location, double value) {
		this._parent.getMatrix().set(location, value);
	}

	@Override
	public void swapRows(int row1, int row2) {
		this._parent.getMatrix().swapRows(row1, row2);
	}

	@Override
	public void swapColumns(int column1, int column2) {
		this._parent.getMatrix().swapColumns(column1, column2);
	}

	@Override
	public void reset() {
		this._parent.reset();
	}

	@Override
	public void clear() {
		this._parent.clear();
	}

}
