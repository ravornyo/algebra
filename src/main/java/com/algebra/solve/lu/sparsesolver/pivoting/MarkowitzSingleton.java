package com.algebra.solve.lu.sparsesolver.pivoting;

import com.algebra.matrix.sparsematrix.ISparseMatrix;
import com.algebra.matrix.sparsematrix.ISparseMatrixElement;
import com.algebra.solve.Pivot;
import com.algebra.solve.PivotInfo;

/**
 * Markowitz-count based strategy for finding a pivot. This strategy will search for
 * singletons (rows or columns with only one element), these can be found rather cheaply.
 * 
 * @author ravornyo
 *
 * @param  The base type
 */
public class MarkowitzSingleton extends MarkowitzSearchStrategy {

	@Override
	public Pivot<ISparseMatrixElement> findPivot(Markowitz markowitz, ISparseMatrix matrix, int eliminationStep, int max) {
		assert(markowitz != null);
        assert(matrix != null);
        
		if (eliminationStep < 1 || eliminationStep > max)
            return Pivot.empty();

        // No singletons left, so don't bother
        if (markowitz.getSingletons() == 0)
            return Pivot.empty();

        // Find the first valid singleton we can use
        int singletons = 0, index;
        for (int i = max + 1; i >= eliminationStep; i--) {
            // First check the current pivot, else
            // search from last to first as this tends to push the higher markowitz
            // products downwards.
            index = i > max ? eliminationStep : i;

            // Not a singleton, let's skip this one...
            if (markowitz.product(index) != 0)
                continue;

            // Keep track of how many singletons we have found
            singletons++;

            /*
             * NOTE: In the sparse library of Spice 3f5 first the diagonal is checked,
             * then the column is checked (if no success go to row) and finally the
             * row is checked for a valid singleton pivot.
             * The diagonal should actually not be checked, as the checking algorithm is the same
             * as for checking the column (FindBiggestInColExclude will not find anything in the column
             * for singletons). The original author did not find this.
             * Also, the original algorithm has a bug in there that renders the whole code invalid...
             * if (ChosenPivot != NULL) { break; } will throw away the pivot even if it was found!
             * (ref. Spice 3f5 Libraries/Sparse/spfactor.c, line 1286)
             */

            // Find the singleton element
            ISparseMatrixElement chosen;
            if (markowitz.columnCount(index) == 0) {
                // The last element in the column is the singleton element!
                chosen = matrix.getLastInColumn(index);
                if (chosen.getRow() <= max && chosen.getColumn() <= max) {
                    // Check if it is a valid pivot
                    double magnitude = Math.abs(chosen.getValue());
                    if (magnitude > markowitz.getAbsolutePivotThreshold())
                        return new Pivot<ISparseMatrixElement>(chosen, PivotInfo.Good);
                }
            }

            // Check if we can still use a row here
            if (markowitz.rowCount(index) == 0){
                // The last element in the row is the singleton element
                chosen = matrix.getLastInRow(index);

                // When the matrix has an empty row, and an RHS element, it is possible
                // that the singleton is not a singleton
                if (chosen == null || chosen.getColumn() < eliminationStep)
                {
                    // The last element is not valid, singleton failed!
                    singletons--;
                    continue;
                }

                // First find the biggest magnitude in the column, not counting the pivot candidate
                ISparseMatrixElement element = chosen.getAbove();
                double largest = 0.0;
                while (element != null && element.getRow() >= eliminationStep) {
                    largest = Math.max(largest, Math.abs(element.getValue()));
                    element = element.getAbove();
                }
                element = chosen.getBelow();
                while (element != null && element.getRow() <= max)
                {
                    largest = Math.max(largest, Math.abs(element.getValue()));
                    element = element.getBelow();
                }

                // Check if the pivot is valid
                if (chosen.getRow() <= max && chosen.getColumn() <= max)
                {
                    double magnitude = Math.abs(chosen.getValue());
                    if (magnitude > markowitz.getAbsolutePivotThreshold() &&
                        magnitude > markowitz.getRelativePivotThreshold() * largest)
                        return new Pivot<ISparseMatrixElement>(chosen, PivotInfo.Good);
                }
            }

            // Don't continue if no more singletons are available
            if (singletons >= markowitz.getSingletons())
                break;
        }

        // All singletons were unacceptable...
        return Pivot.empty();
	}

}
