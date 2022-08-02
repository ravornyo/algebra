package com.algebra.solve.lu.sparsesolver.pivoting;

import com.algebra.matrix.sparsematrix.ISparseMatrix;
import com.algebra.matrix.sparsematrix.ISparseMatrixElement;
import com.algebra.solve.Pivot;
import com.algebra.solve.PivotInfo;

/**
 * Markowitz-count based strategy for finding a pivot. Searches the whole diagonal of the submatrix.
 * 
 * @author ravornyo
 *
 * @param  Base type
 */
public class MarkowitzDiagonal extends MarkowitzSearchStrategy {
	
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

        int minMarkowitzProduct = Integer.MAX_VALUE;
        ISparseMatrixElement chosen = null;
        double ratioOfAccepted = 0.0;
        int ties = 0;

        /* Used for debugging alongside Spice 3f5
        for (var index = matrix.Size + 1; index > eliminationStep; index--)
        {
            var i = index > matrix.Size ? eliminationStep : index; */
        for (int i = eliminationStep; i <= max; i++) {
            // Skip the diagonal if we already have a better one
            if (markowitz.product(i) > minMarkowitzProduct)
                continue;

            // Get the diagonal
            ISparseMatrixElement diagonal = matrix.findDiagonalElement(i);
            if (diagonal == null)
                continue;

            // Get the magnitude
            double magnitude = Math.abs(diagonal.getValue());
            if (magnitude <= markowitz.getAbsolutePivotThreshold())
                continue;

            // Check that the pivot is eligible
            double largest = 0.0;
            ISparseMatrixElement element = diagonal.getBelow();
            while (element != null && element.getRow() <= max){
                largest = Math.max(largest, Math.abs(element.getValue()));
                element = element.getBelow();
            }
            element = diagonal.getAbove();
            while (element != null && element.getRow() >= eliminationStep) {
                largest = Math.max(largest, Math.abs(element.getValue()));
                element = element.getAbove();
            }
            if (magnitude <= markowitz.getRelativePivotThreshold() * largest)
                continue;

            // Check markowitz numbers to find the optimal pivot
            if (markowitz.product(i) < minMarkowitzProduct) {
                // Notice strict inequality, this is a new smallest product
                chosen = diagonal;
                minMarkowitzProduct = markowitz.product(i);
                ratioOfAccepted = largest / magnitude;
                ties = 0;
            }
            else {
                // If we have enough elements with the same (minimum) number of ties, stop searching
                ties++;
                double ratio = largest / magnitude;
                if (ratio < ratioOfAccepted) {
                    chosen = diagonal;
                    ratioOfAccepted = ratio;
                }
                if (ties >= minMarkowitzProduct * TIES_MULTIPLIER)
                    return new Pivot<ISparseMatrixElement>(chosen, PivotInfo.Suboptimal);
            }
        }

        // The chosen pivot has already been checked for validity
        return chosen != null ? new Pivot<ISparseMatrixElement>(chosen, PivotInfo.Suboptimal) : Pivot.empty();
	}

}
