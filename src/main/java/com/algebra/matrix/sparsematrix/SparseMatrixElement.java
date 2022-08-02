package com.algebra.matrix.sparsematrix;

import com.algebra.Element;
import com.algebra.matrix.MatrixLocation;

/**
 * An element for a sparse matrix.
 * 
 * @author avornyo
 *
 * @param  The base type
 */
public class SparseMatrixElement extends Element implements ISparseMatrixElement{
	
	private int _row;
	private int _column;
	private ISparseMatrixElement _left;
	private ISparseMatrixElement _right;
	private ISparseMatrixElement _above;
	private ISparseMatrixElement _below;
	
	public SparseMatrixElement(MatrixLocation location){
        setValue(0.0);
        setRow(location.getRow());
        setColumn(location.getColumn());
    }
	/**
	 *  Gets the row index of the matrix element
	 *  
	 * @return The row index
	 */
	public int getRow() {
		return this._row;
	}
	/**
	 * Sets the row index of the matrix element
	 * 
	 * @param row The row index
	 */
	public void setRow(int row) {
		this._row = row;
	}
	/**
	 *  Gets the column index of the matrix element
	 *  
	 * @return The column index
	 */
	public int getColumn() {
		return this._column;
	}
	/**
	 * Sets the column index of the matrix element
	 * 
	 * @param row The column index
	 */
	public void setColumn(int column) {
		this._column = column;
	}
	/**
	 * Gets the nearest SparseMatrixElement left of this one
	 * 
	 * @return The matrix element.
	 */
	public ISparseMatrixElement getLeft() {
		return this._left;
	}
	/**
	 * Sets the nearest SparseMatrixElement left of this one
	 * 
	 * @param left The matrix element.
	 */
	@Override
	public void setLeft(ISparseMatrixElement left) {
		this._left = left;
	}
	/**
	 * Gets the nearest SparseMatrixElement right of this one
	 * 
	 * @return The matrix element.
	 */
	public ISparseMatrixElement getRight() {
		return this._right;
	}
	/**
	 * Sets the nearest SparseMatrixElement right of this one
	 * @param right The matrix element.
	 */
	public void setRight(ISparseMatrixElement right) {
		this._right = right;
	}
	/**
	 * Gets the nearest SparseMatrixElement above this one
	 * 
	 * @return The matrix element.
	 */
	public ISparseMatrixElement getAbove() {
		return this._above;
	}
	/**
	 * Sets the nearest SparseMatrixElement above of this one
	 * 
	 * @param above The matrix element.
	 */
	@Override
	public void setAbove(ISparseMatrixElement above) {
		this._above = above;
	}
	/**
	 * Gets the nearest SparseMatrixElement below this one
	 * 
	 * @return The matrix element.
	 */
	public ISparseMatrixElement getBelow() {
		return this._below;
	}
	/**
	 * Sets the nearest SparseMatrixElement below of this one
	 * 
	 * @param below The matrix element.
	 */
	@Override
	public void setBelow(ISparseMatrixElement below) {
		this._below = below;
	}

}
