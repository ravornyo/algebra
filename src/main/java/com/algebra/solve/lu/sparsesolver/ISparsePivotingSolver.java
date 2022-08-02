package com.algebra.solve.lu.sparsesolver;

import com.algebra.matrix.sparsematrix.ISparseMatrix;
import com.algebra.solve.IPivotingSolver;
import com.algebra.vector.sparsevector.ISparseVector;
/**
 * A sparse solver that can use pivoting to solve equations.
 * 
 * @author avornyo
 *
 * @param  The base value type.
 */
public interface ISparsePivotingSolver extends ISparseSolver, IPivotingSolver<ISparseMatrix, ISparseVector>{

}
