package com.algebra.solve.lu.densesolver;

import com.algebra.matrix.IMatrix;
import com.algebra.matrix.MatrixLocation;
import com.algebra.solve.Pivot;
import com.algebra.solve.PivotInfo;

/**
 * Rook pivoting strategy.
 * 
 * @author ravornyo
 *
 * @param  The base type
 */
public class RookPivoting{
	
    private double _absolutePivotThreshold = 1e-13;
    private double _relativePivotThreshold = 1e-3;

    public Pivot<MatrixLocation> findPivot(IMatrix matrix, int eliminationStep, int max){
        assert(matrix != null);
        
        if (eliminationStep < 1 || eliminationStep > max)
            return Pivot.empty();

        // Find the largest element below and right of the pivot
        double largest = Math.abs(matrix.get(eliminationStep, eliminationStep));
        MatrixLocation loc = new MatrixLocation(eliminationStep, eliminationStep);

        // We just select the biggest off-diagonal element that we can find!
        for (int i = eliminationStep + 1; i <= max; i++) {
            double c = Math.abs(matrix.get(eliminationStep, i));
            if (c > largest) {
                largest = c;
                loc = new MatrixLocation(eliminationStep, i);
            }

            c = Math.abs(matrix.get(i, eliminationStep));
            if (c > largest) {
                largest = c;
                loc = new MatrixLocation(i, eliminationStep);
            }
        }
        return largest > 0.0 ? new Pivot<MatrixLocation>(loc, PivotInfo.Good) : Pivot.empty();
    }
    
    public boolean isValidPivot(IMatrix matrix, int eliminationStep, int max) {
    	assert(matrix != null);

        // Get the magnitude of the current pivot
        double magnitude = Math.abs(matrix.get(eliminationStep, eliminationStep));
        if (magnitude <= getAbsolutePivotThreshold())
            return false;

        // Search for the largest element below the pivot
        double largest = 0.0;
        for (int i = eliminationStep + 1; i <= max; i++) {
            largest = Math.max(largest, Math.abs(matrix.get(eliminationStep, i)));
            largest = Math.max(largest, Math.abs(matrix.get(i, eliminationStep)));
        }

        // Check the validity
        if (magnitude > largest * getRelativePivotThreshold())
            return true;
        return false;
    }
    
	public double getAbsolutePivotThreshold() {
		return _absolutePivotThreshold;
	}
	
	public void setAbsolutePivotThreshold(double _absolutePivotThreshold) {
		this._absolutePivotThreshold = _absolutePivotThreshold;
	}
	
	public double getRelativePivotThreshold() {
		return _relativePivotThreshold;
	}
	
	public void setRelativePivotThreshold(double _relativePivotThreshold) {
		this._relativePivotThreshold = _relativePivotThreshold;
	}

}
