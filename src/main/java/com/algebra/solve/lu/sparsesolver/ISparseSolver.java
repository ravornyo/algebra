package com.algebra.solve.lu.sparsesolver;

import com.algebra.matrix.MatrixLocation;
import com.algebra.matrix.sparsematrix.ISparseMatrixElement;
import com.algebra.vector.sparsevector.ISparseVectorElement;
/**
 * An ISolver that uses sparse elements internally.
 * 
 * @author avornyo
 *
 * @param  The base value type.
 */
public interface ISparseSolver {
	/**
	 * Finds the element at the specified location in the matrix.
	 * 
	 * @param location The location.
	 * @return The element if it exists; otherwise null
	 */
	ISparseMatrixElement findElement(MatrixLocation location);
    /**
     * Finds the element at the specified position in the right-hand side vector.
     * 
     * @param row The row index
     * @return The element if it exists; otherwise null
     */
	ISparseVectorElement findElement(int row);
    /**
     * Gets the element at the specified location in the matrix. A new element is
     * created if it doesn't exist yet.
     * 
     * @param location The location.
     * @return
     */
	ISparseMatrixElement getElement(MatrixLocation location);
    /**
     * Gets the element at the specified position in the right-hand side vector.
     * created if it doesn't exist yet.
     * 
     * @param row The row index
     * @return
     */
	ISparseVectorElement getElement(int row);
    /**
     * Removes a matrix element at the specified location.
     * 
     * @param location The location.
     * @return true if the element was removed; otherwise, false
     */
    public boolean removeElement(MatrixLocation location);
    /**
     * Removes a right-hand side vector element.
     * 
     * @param row The row index
     * @return true if the element was removed; otherwise, false
     */
    public boolean removeElement(int row);
}
