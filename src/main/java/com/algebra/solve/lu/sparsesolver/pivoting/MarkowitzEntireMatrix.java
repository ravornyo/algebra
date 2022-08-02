package com.algebra.solve.lu.sparsesolver.pivoting;

import com.algebra.matrix.sparsematrix.ISparseMatrix;
import com.algebra.matrix.sparsematrix.ISparseMatrixElement;
import com.algebra.solve.Pivot;
import com.algebra.solve.PivotInfo;

/**
 * Markowitz-count based strategy for finding a pivot. Search the complete submatrix.
 * 
 * @author ravornyo
 *
 * @param 
 */
public class MarkowitzEntireMatrix extends MarkowitzSearchStrategy {

	/**
	 * A heuristic for speeding up pivot searching.
	 * 
	 * The multiplier for searching pivots with the same markowitz products.
	 * 
	 * Instead of searching the whole matrix for a pivot on the diagonal, the search strategy can
	 * choose to stop searching for more pivot elements with the lowest "Markowitz product", which
	 * scores how many extra unwanted elements a row/column could create as a by-product of factoring
	 * the solver. When this score is tied, this search strategy will keep searching until we have
	 * (MarkowitzProduct * TiesMultiplier) eligible pivots. In other words, pivots with a high
	 * Markowitz product will ask the search strategy for more entries to make sure that we can't do 
	 * better.
	 * 
	 */
	private static final int TIES_MULTIPLIER = 5;

	@Override
	public Pivot<ISparseMatrixElement> findPivot(Markowitz markowitz, ISparseMatrix matrix, int eliminationStep, int max) {
		assert(markowitz != null);
        assert(matrix != null);
        
        if (eliminationStep < 1 || eliminationStep > max)
            return Pivot.empty();

        ISparseMatrixElement chosen = null;
        long minMarkowitzProduct = Long.MAX_VALUE;
        double largestMagnitude = 0.0, acceptedRatio = 0.0;
        ISparseMatrixElement largestElement = null;
        int ties = 0;

        // Start search of matrix on column by column basis
        for (int i = eliminationStep; i <= max; i++) {
            // Find an entry point to the interesting part of the column
            ISparseMatrixElement lowest = matrix.getLastInColumn(i);
            while (lowest != null && lowest.getRow() > max)
                lowest = lowest.getAbove();
            if (lowest == null || lowest.getRow() < eliminationStep)
                continue;

            // Find the biggest magnitude in the column for checking valid pivots later
            double largest = 0.0;
            ISparseMatrixElement element = lowest;
            while (element != null && element.getRow() >= eliminationStep){
                largest = Math.max(largest, Math.abs(element.getValue()));
                element = element.getAbove();
            }
            if (largest == 0.0)
                continue;

            // Restart search for a pivot
            element = lowest;
            while (element != null && element.getRow() >= eliminationStep) {
                // Find the magnitude and Markowitz product
                double magnitude = Math.abs(element.getValue());
                int product = markowitz.rowCount(element.getRow()) * markowitz.columnCount(element.getColumn());

                // In the case no valid pivot is available, at least return the largest element
                if (magnitude > largestMagnitude) {
                    largestElement = element;
                    largestMagnitude = magnitude;
                }

                // test to see if the element is acceptable as a pivot candidate
                if (product <= minMarkowitzProduct
                    && magnitude > markowitz.getRelativePivotThreshold() * largest
                    && magnitude > markowitz.getAbsolutePivotThreshold()) {
                    // Test to see if the element has the lowest Markowitz product yet found,
                    // or whether it is tied with an element found earlier
                    if (product < minMarkowitzProduct){
                        // Notice strict inequality
                        // This is a new smallest Markowitz product
                        chosen = element;
                        minMarkowitzProduct = product;
                        acceptedRatio = largest / magnitude;
                        ties = 0;
                        
                    } else {
                        // This case handles Markowitz ties
                        ties++;
                        double ratio = largest / magnitude;
                        if (ratio < acceptedRatio) {
                            chosen = element;
                            acceptedRatio = ratio;
                        }
                        if (ties >= minMarkowitzProduct * TIES_MULTIPLIER)
                            return new Pivot<ISparseMatrixElement>(chosen, PivotInfo.Suboptimal);
                    }
                }

                element = element.getAbove();
            }
        }

        // If a valid pivot was found, return it
        if (chosen != null)
            return new Pivot<ISparseMatrixElement>(chosen, PivotInfo.Suboptimal);

        // Else just return the largest element
        if (largestElement == null)
            return Pivot.empty();
        return new Pivot<ISparseMatrixElement>(largestElement, PivotInfo.Bad);
	}

}
