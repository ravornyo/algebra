package com.algebra.matrix.sparsematrix;

import com.algebra.matrix.IMatrix;
import com.algebra.matrix.MatrixLocation;
/**
 * Describes a sparse matrix that return elements that have links to neighboring non-zero elements.
 * 
 * @author avornyo
 *
 * @param  The base type
 */
public interface ISparseMatrix extends IMatrix{
	/**
	 * Gets the number of elements in the matrix.
	 * 
	 * @return The element count
	 */
	int getElementCount();
	/**
	 * Gets the first non-default ISparseMatrixElement in the specified row
	 * 
	 * @param row The row index
	 * @return The matrix element
	 */
	ISparseMatrixElement getFirstInRow(int row);
	/**
	 * Gets the last non-default ISparseMatrixElement in the specified row
	 * 
	 * @param row The row index
	 * @return The matrix element
	 */
	ISparseMatrixElement getLastInRow(int row);
	/**
	 * Gets the first non-default ISparseMatrixElement in the specified column
	 * 
	 * @param column The column index
	 * @return The matrix element
	 */
	ISparseMatrixElement getFirstInColumn(int column);
	/**
	 * Gets the last non-default ISparseMatrixElement in the specified column
	 * 
	 * @param column The column index
	 * @return The matrix element
	 */
	ISparseMatrixElement getLastInColumn(int column);
	/**
	 * Finds the ISparseMatrixElement on the diagonal
	 * 
	 * @param index
	 * @return The matrix element if it exists; otherwise null
	 */
	ISparseMatrixElement findDiagonalElement(int index);
	/**
	 * Gets a pointer to the matrix element at the specified row and column. If
     * the element doesn't exist, it is created.
	 * 
	 * @param location
	 * @return The matrix element if it exists; otherwise null
	 */
	ISparseMatrixElement getElement(MatrixLocation location);
	/**
	 * Finds a pointer to the matrix element at the specified row and column. If
     * the element doesn't exist, null is returned.
     * 
	 * @param location The matrix location
	 * @return The matrix element if it exists; otherwise null
	 */
	ISparseMatrixElement findElement(MatrixLocation location);
	/**
	 * Removes a matrix element at the specified row and column. If the element
     * doesn't exist, this method returns false.
     * 
	 * @param location The matrix location
	 * @return
	 */
	boolean removeElement(MatrixLocation location);
}
