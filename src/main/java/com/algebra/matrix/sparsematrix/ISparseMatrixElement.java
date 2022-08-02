package com.algebra.matrix.sparsematrix;

import com.algebra.IElement;

/**
 * A matrix element for an ISparseMatrix. This element has links
 * to the surrounding matrix elements.
 * 
 * @author avornyo
 *
 * @param  The base type
 */
public interface ISparseMatrixElement extends IElement{
	/**
	 *  Gets the row index of the matrix element
	 *  
	 * @return The row index
	 */
	int getRow();
	/**
	 * Sets the row index of the matrix element
	 * 
	 * @param row The row index
	 */
	void setRow(int row);
	/**
	 *  Gets the column index of the matrix element
	 *  
	 * @return The column index
	 */
	int getColumn();
	/**
	 * Sets the column index of the matrix element
	 * 
	 * @param row The column index
	 */
	void setColumn(int column);
	/**
	 * Gets the nearest ISparseMatrixElement left of this one
	 * 
	 * @return The matrix element.
	 */
	ISparseMatrixElement getLeft();
	/**
	 * Sets the nearest ISparseMatrixElement left of this one
	 * @param left The matrix element.
	 */
	void setLeft(ISparseMatrixElement left);
	/**
	 * Gets the nearest ISparseMatrixElement right of this one
	 * 
	 * @return The matrix element.
	 */
	ISparseMatrixElement getRight();
	/**
	 * Sets the nearest ISparseMatrixElement right of this one
	 * @param right The matrix element.
	 */
	void setRight(ISparseMatrixElement right);
	/**
	 * Gets the nearest ISparseMatrixElement above this one
	 * 
	 * @return The matrix element.
	 */
	ISparseMatrixElement getAbove();
	/**
	 * Sets the nearest ISparseMatrixElement above of this one
	 * @param above The matrix element.
	 */
	void setAbove(ISparseMatrixElement above);
	/**
	 * Gets the nearest ISparseMatrixElement below this one
	 * 
	 * @return The matrix element.
	 */
	ISparseMatrixElement getBelow();
	/**
	 * Sets the nearest ISparseMatrixElement below of this one
	 * @param below The matrix element.
	 */
	void setBelow(ISparseMatrixElement below);
}
