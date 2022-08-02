package com.algebra.solve.lu.sparsesolver;

import com.algebra.matrix.MatrixLocation;
import com.algebra.matrix.sparsematrix.ISparseMatrix;
import com.algebra.matrix.sparsematrix.ISparseMatrixElement;

public class SparseReorderedMatrix implements ISparseMatrix {

	private final SparseLUSolver _parent;

	public SparseReorderedMatrix(SparseLUSolver parent) {
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
		this._parent.swapRows(row1, row2);
	}

	@Override
	public void swapColumns(int column1, int column2) {
		this._parent.swapColumns(column1, column2);
	}

	@Override
	public void reset() {
		this._parent.reset();
	}

	@Override
	public void clear() {
		this._parent.clear();
	}

	@Override
	public int getElementCount() {
		return this._parent.getMatrix().getElementCount();
	}

	@Override
	public ISparseMatrixElement getFirstInRow(int row) {
		return this._parent.getMatrix().getFirstInRow(row);
	}

	@Override
	public ISparseMatrixElement getLastInRow(int row) {
		return this._parent.getMatrix().getLastInRow(row);
	}

	@Override
	public ISparseMatrixElement getFirstInColumn(int column) {
		return this._parent.getMatrix().getFirstInColumn(column);
	}

	@Override
	public ISparseMatrixElement getLastInColumn(int column) {
		return this._parent.getMatrix().getLastInColumn(column);
	}

	@Override
	public ISparseMatrixElement findDiagonalElement(int index) {
		return this._parent.getMatrix().findDiagonalElement(index);
	}

	@Override
	public ISparseMatrixElement getElement(MatrixLocation location) {
		return this._parent.getMatrix().getElement(location);
	}

	@Override
	public ISparseMatrixElement findElement(MatrixLocation location) {
		return this._parent.getMatrix().findElement(location);
	}

	@Override
	public boolean removeElement(MatrixLocation location) {
		return this._parent.getMatrix().removeElement(location);
	}

}
