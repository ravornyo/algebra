package com.algebra.solve.lu.sparsesolver.pivoting;

import java.util.ArrayList;
import java.util.List;

import com.algebra.matrix.sparsematrix.ISparseMatrix;
import com.algebra.matrix.sparsematrix.ISparseMatrixElement;
import com.algebra.solve.Pivot;
import com.algebra.solve.PivotInfo;

/**
 * Markowitz-based pivot search. Quickly search the diagonal for valid pivots.
 * 
 * @author ravornyo
 *
 * @param  The base value type.
 */
public class MarkowitzQuickDiagonal extends MarkowitzSearchStrategy {

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
	
	private static final int MAX_TIES = 100;
	
	private final List<ISparseMatrixElement> _tiedElements = new ArrayList<ISparseMatrixElement>(MAX_TIES);
	
	public MarkowitzQuickDiagonal() {
		for(int i=0; i < MAX_TIES; i++) {
			_tiedElements.add(null);
		}
	}
		
	@Override
	public Pivot<ISparseMatrixElement> findPivot(Markowitz markowitz, ISparseMatrix matrix, int eliminationStep, int max) {
		assert(markowitz != null);
        assert(matrix != null);
        
        if (eliminationStep < 1 || eliminationStep > max)
            return Pivot.empty();

        int minMarkowitzProduct = Integer.MAX_VALUE;
        int numberOfTies = -1;

        /* Used for debugging along Spice 3f5
        for (var index = matrix.Size + 1; index > eliminationStep; index--)
        {
            int i = index > matrix.Size ? eliminationStep : index; */
        for (int i = eliminationStep; i <= max; i++) {
            // Skip diagonal elements with a Markowitz product worse than already found
            int product = markowitz.product(i);
            if (product >= minMarkowitzProduct)
                continue;

            // Get the diagonal item
            ISparseMatrixElement diagonal = matrix.findDiagonalElement(i);
            if (diagonal == null)
                continue;

            // Get the magnitude
            double magnitude = Math.abs(diagonal.getValue());
            if (magnitude <= markowitz.getAbsolutePivotThreshold())
                continue;

            // Well, can't do much better than this can we? (Assuming all the singletons are taken)
            // Note that a singleton can still appear depending on the allowed tolerances!
            if (product == 1) {
                // Find the off-diagonal elements
                ISparseMatrixElement otherInRow = (diagonal.getRight() != null)?  diagonal.getRight() : diagonal.getLeft();
                ISparseMatrixElement otherInColumn = (diagonal.getBelow() != null)?  diagonal.getBelow() : diagonal.getAbove(); 

                // Accept diagonal as pivot if diagonal is larger than off-diagonals and
                // the off-diagonals are placed symmetrically
                if (otherInRow != null && otherInColumn != null) {
                    if (otherInRow.getColumn() == otherInColumn.getRow()) {
                        double largest = Math.max(
                        		Math.abs(otherInRow.getValue()),
                        		Math.abs(otherInColumn.getValue()));
                        if (magnitude >= largest)
                            return new Pivot<ISparseMatrixElement>(diagonal, PivotInfo.Good);
                    }
                }
            }

            if (product < minMarkowitzProduct) {
                // We found a diagonal that beats all the previous ones!
                numberOfTies = 0;
                _tiedElements.set(0, diagonal);
                minMarkowitzProduct = product;
            } else {
                if (numberOfTies < _tiedElements.size() - 1) {
                    // Keep track of this diagonal too
                    _tiedElements.set(++numberOfTies, diagonal);

                    // This is our heuristic for speeding up pivot searching
                    if (numberOfTies >= minMarkowitzProduct * TIES_MULTIPLIER)
                        break;
                }
            }
        }

        // Not even one eligible pivot on the diagonal...
        if (numberOfTies < 0)
            return Pivot.empty();

        // Determine which of the tied elements is the best numerical choise
        ISparseMatrixElement chosen = null;
        double maxRatio = 1.0 / markowitz.getRelativePivotThreshold();
        for (int i = 0; i <= numberOfTies; i++) {
            ISparseMatrixElement diag = _tiedElements.get(i);
            double mag = Math.abs(diag.getValue());
            double largest = largestOtherElementInColumn(markowitz, diag, eliminationStep, max);
            double ratio = largest / mag;
            if (ratio < maxRatio) {
                maxRatio = ratio;
                chosen = diag;
            }
        }

        // We don't actually know if the pivot is sub-optimal, but we take the worst case scenario.
        return chosen != null ? new Pivot<ISparseMatrixElement>(chosen, PivotInfo.Suboptimal) : Pivot.empty();
	}

	private double largestOtherElementInColumn(Markowitz markowitz, ISparseMatrixElement chosen, int eliminationStep, int max){
        // Find the biggest element above and below the pivot
        ISparseMatrixElement element = chosen.getBelow();
        double largest = 0.0;
        while (element != null && element.getRow() <= max) {
            largest = Math.max(largest, Math.abs(element.getValue()));
            element = element.getBelow();
        }
        element = chosen.getAbove();
        while (element != null && element.getRow() >= eliminationStep)
        {
            largest = Math.max(largest, Math.abs(element.getValue()));
            element = element.getAbove();
        }
        return largest;
    }
}
