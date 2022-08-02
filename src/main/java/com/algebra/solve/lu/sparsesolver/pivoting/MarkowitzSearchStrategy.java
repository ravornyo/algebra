package com.algebra.solve.lu.sparsesolver.pivoting;

import com.algebra.matrix.sparsematrix.ISparseMatrix;
import com.algebra.matrix.sparsematrix.ISparseMatrixElement;
import com.algebra.solve.Pivot;

/**
 * A template for a search strategy for finding pivots. It is
 * used for implementing the strategy outlined by Markowitz.
 * 
 * @author avornyo
 *
 * @param  The base type
 */
public abstract class MarkowitzSearchStrategy {
	/**
	 * Find a pivot in a matrix.
	 * 
	 * @param markowitz The Markowitz pivot strategy.
	 * @param matrix The matrix.
	 * @param eliminationStep The current elimination step.
	 * @param max The maximum row/column index.
	 * @return The pivot element, or null if no pivot was found.
	 */
	public abstract Pivot<ISparseMatrixElement> findPivot(Markowitz markowitz, ISparseMatrix matrix, int eliminationStep, int max);
}
