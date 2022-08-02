package com.algebra.solve.lu.sparsesolver.pivoting;

import java.util.ArrayList;
import java.util.List;

import com.algebra.matrix.IMatrix;
import com.algebra.matrix.sparsematrix.ISparseMatrix;
import com.algebra.matrix.sparsematrix.ISparseMatrixElement;
import com.algebra.solve.Pivot;
import com.algebra.solve.PivotInfo;
import com.algebra.vector.sparsevector.ISparseVector;
import com.algebra.vector.sparsevector.ISparseVectorElement;
/**
 * A search strategy based on methods outlined by Markowitz.
 * 
 * @author avornyo
 *
 * @param  The base type
 */
public class Markowitz {
	/**
    * The maximum Markowitz count that will not result in Int32 overflow when squared
    * Markowitz counts are capped at this quantity.
    *
    * To reach this quantity, a variable would have to be connected to this amount of
    * other varibles. We could say that this is highly unlikely. In the event that this
    * amount does get reached, we would probably need to do a sanity check.
    */
	public static final int MAX_COUNT = 46340;
	/**
	 * The absolute threshold for choosing a pivot.
	 */
	private double _absolutePivotThreshold = 1e-13;
	/**
	 * The relative threshold for choosing a pivot.
	 */
    private double _relativePivotThreshold = 1e-3;
    /**
     * the number of singletons.
     */
    private int _singletons;
    
    private int[] _markowitzRow;
    private int[] _markowitzColumn;
    private int[] _markowitzProduct;
	
	private List<MarkowitzSearchStrategy> _strategies;
	
    public Markowitz() {
		this._strategies = new ArrayList<MarkowitzSearchStrategy>();
		// Register default strategies
		this._strategies.add(new MarkowitzSingleton());
		this._strategies.add(new MarkowitzQuickDiagonal());
		this._strategies.add(new MarkowitzDiagonal());
		this._strategies.add(new MarkowitzEntireMatrix());
	}   
    /**
     * This method will check whether or not a pivot element is valid or not.
     * It checks for the submatrix right/below of the pivot.
     * 
     * @param pivot The pivot candidate.
     * @param max The maximum index that a pivot can have.
     * @return True if the pivot can be used.
     */
    public boolean isValidPivot(ISparseMatrixElement pivot, int max) {
        assert(pivot != null);
        
        if (pivot.getRow() > max || pivot.getColumn() > max)
            return false;

        // Get the magnitude of the current pivot
        double magnitude = Math.abs(pivot.getValue());

        // Search for the largest element below the pivot
        ISparseMatrixElement element = pivot.getBelow();
        double largest = 0.0;
        while (element != null && element.getRow() <= max) {
            largest = Math.max(largest, Math.abs(element.getValue()));
            element = element.getBelow();
        }

        // Check the validity
        if (largest * getRelativePivotThreshold() < magnitude)
            return true;
        return false;
    }
    /**
     * Initializes the pivot searching algorithm.
     * 
     * @param matrix The matrix to use for initialization.
     */
    public void initialize(IMatrix matrix) {
        assert(matrix != null);

        // Allocate arrays
        _markowitzRow = new int[matrix.getSize() + 1];
        _markowitzColumn = new int[matrix.getSize() + 1];
        _markowitzProduct = new int[matrix.getSize() + 2];
    }
    /**
     * Clears the pivot strategy.
     */
    public void clear() {
        _markowitzRow = null;
        _markowitzColumn = null;
        _markowitzProduct = null;
    }
    /**
     * 
     * @param matrix
     * @param rhs
     * @param step
     * @param max
     */
    private void count(ISparseMatrix matrix, ISparseVector rhs, int step, int max) {
        ISparseMatrixElement element;

        // Get the first element in the vector
        ISparseVectorElement rhsElement = rhs.getFirstInVector();

        // Generate Markowitz row count
        for (int i = max; i >= step; i--){
            // Set count to -1 initially to remove count due to pivot element
            int count = -1;
            element = matrix.getFirstInRow(i);
            while (element != null && element.getColumn() < step)
                element = element.getRight();
            while (element != null){ // We want to count the elements outside the limit as well
                count++;
                element = element.getRight();
            }

            // Include elements on the Rhs vector
            while (rhsElement != null && rhsElement.getIndex() < step)
                rhsElement = rhsElement.getBelow();
            if (rhsElement != null && rhsElement.getIndex() == i)
                count++;

            _markowitzRow[i] = Math.min(count, Markowitz.MAX_COUNT);
        }

        // Generate Markowitz column count
        for (int i = step; i <= max; i++){
            // Set count to -1 initially to remove count due to pivot element
            int count = -1;
            element = matrix.getFirstInColumn(i);
            while (element != null && element.getRow() < step)
                element = element.getBelow();
            while (element != null) {
                count++;
                element = element.getBelow();
            }
            _markowitzColumn[i] = Math.min(count, Markowitz.MAX_COUNT);
        }
    }
    
    private void products(int step, int max) {
        this._singletons = 0;
        for (int i = step; i <= max; i++){
            // UpdateMarkowitzProduct(i);
            _markowitzProduct[i] = _markowitzRow[i] * _markowitzColumn[i];
            if (_markowitzProduct[i] == 0)
                this._singletons++;
        }
    }
    /**
     * Setup the pivot strategy.
     * 
     * @param matrix The matrix.
     * @param rhs The right-hand side vector.
     * @param eliminationStep The current elimination step
     * @param max The maximum row/column index.
     */
    public void setup(ISparseMatrix matrix, ISparseVector rhs, int eliminationStep, int max) {
        assert(matrix != null);
        assert(rhs != null);

        // Initialize Markowitz row, column and product vectors if necessary
        if (this._markowitzRow == null || _markowitzRow.length != matrix.getSize() + 1)
            initialize(matrix);
        count(matrix, rhs, eliminationStep, max);
        products(eliminationStep, max);
    }
    /**
     * Move the pivot to the diagonal for this elimination step.
     * This is done by swapping the rows and columns of the diagonal and that of the pivot.
     * 
     * @param matrix The matrix.
     * @param rhs The right-hand side vector
     * @param pivot The pivot element
     * @param eliminationStep The elimination step
     */
    public void movePivot(ISparseMatrix matrix, ISparseVector rhs, ISparseMatrixElement pivot, int eliminationStep){
    	assert(matrix != null);
        assert(rhs != null);
        assert(pivot != null);

        // If we haven't setup, just skip
        if (_markowitzProduct == null)
            return;
        int oldProduct;

        int row = pivot.getRow();
        int col = pivot.getColumn();

        // If the pivot is a singleton, then we just consumed it
        if (_markowitzProduct[row] == 0 || _markowitzProduct[col] == 0)
            this._singletons--;

        // Exchange rows
        if (row != eliminationStep){
            // Swap row Markowitz numbers
            int tmp = _markowitzRow[row];
            _markowitzRow[row] = _markowitzRow[eliminationStep];
            _markowitzRow[eliminationStep] = tmp;

            // Update the Markowitz product
            oldProduct = _markowitzProduct[row];
            _markowitzProduct[row] = _markowitzRow[row] * _markowitzColumn[row];
            if (oldProduct == 0){
                if (_markowitzProduct[row] != 0)
                    _singletons--;
            } else {
                if (_markowitzProduct[row] == 0)
                    _singletons++;
            }
        }

        // Exchange columns
        if (col != eliminationStep) {
            // Swap column Markowitz numbers
            int tmp = _markowitzColumn[col];
            _markowitzColumn[col] = _markowitzColumn[eliminationStep];
            _markowitzColumn[eliminationStep] = tmp;

            // Update the Markowitz product
            oldProduct = _markowitzProduct[col];
            _markowitzProduct[col] = _markowitzRow[col] * _markowitzColumn[col];
            if (oldProduct == 0) {
                if (_markowitzProduct[col] != 0)
                    _singletons--;
            } else {
                if (_markowitzProduct[col] == 0)
                    _singletons++;
            }
        }

        // Also update the moved pivot
        oldProduct = _markowitzProduct[eliminationStep];
        _markowitzProduct[eliminationStep] = _markowitzRow[eliminationStep] * _markowitzColumn[eliminationStep];
        if (oldProduct == 0) {
            if (_markowitzProduct[eliminationStep] != 0)
                _singletons--;
        } else {
            if (_markowitzProduct[eliminationStep] == 0)
                _singletons++;
        }
    }
    /**
     * Update the strategy after the pivot was moved.
     * 
     * @param matrix The matrix.
     * @param pivot The pivot element.
     * @param limit The maximum row/column for pivots.
     */
    public void update(ISparseMatrix matrix, ISparseMatrixElement pivot, int limit) {
    	assert(matrix != null);
        assert(pivot != null);

        // If we haven't setup, just skip
        if (_markowitzProduct == null)
            return;

        // Go through all elements below the pivot. If they exist, then we can subtract 1 from the Markowitz row vector!
        for (ISparseMatrixElement column = pivot.getBelow(); column != null && column.getRow() <= limit; column = column.getBelow()) {
            int row = column.getRow();

            // Update the Markowitz product
            _markowitzProduct[row] -= _markowitzColumn[row];
            --_markowitzRow[row];

            // If we reached 0, then the row just turned to a singleton row
            if (_markowitzRow[row] == 0)
                _singletons++;
        }

        // go through all elements right of the pivot. For every element, we can subtract 1 from the Markowitz column vector!
        for (ISparseMatrixElement row = pivot.getRight(); row != null && row.getColumn() <= limit; row = row.getRight()) {
            int column = row.getColumn();

            // Update the Markowitz product
            _markowitzProduct[column] -= _markowitzRow[column];
            --_markowitzColumn[column];

            // If we reached 0, then the column just turned to a singleton column
            // This only adds a singleton if the row wasn't detected as a singleton row first
            if (_markowitzColumn[column] == 0 && _markowitzRow[column] != 0)
                _singletons++;
        }
    }
    /**
     * Notifies the strategy that a fill-in has been created
     * 
     * @param matrix The matrix.
     * @param fillin The fill-in.
     */
    public void createFillin(ISparseMatrix matrix, ISparseMatrixElement fillin) {
        assert(matrix != null);
        assert(fillin != null);

        if (_markowitzProduct == null)
            return;

        // Update the markowitz row count
        int index = fillin.getRow();
        _markowitzRow[index]++;
        _markowitzProduct[index] =
            Math.min(_markowitzRow[index] * _markowitzColumn[index], Markowitz.MAX_COUNT);
        if (_markowitzRow[index] == 1 && _markowitzColumn[index] != 0)
            _singletons--;

        // Update the markowitz column count
        index = fillin.getColumn();
        _markowitzColumn[index]++;
        _markowitzProduct[index] =
            Math.min(_markowitzRow[index] * _markowitzColumn[index], Markowitz.MAX_COUNT);
        if (_markowitzRow[index] != 0 && _markowitzColumn[index] == 1)
            _singletons--;
    }
    /**
     * Find a pivot in the matrix.
     * 
     * The pivot should be searched for in the submatrix towards the right and down of the
     * current diagonal at row/column eliminationStep. This pivot element
     * will be moved to the diagonal for this elimination step.
     * 
     * @param matrix The matrix.
     * @param eliminationStep The current elimination step.
     * @param max The maximum row/column index of any pivot.
     * @return
     */
    public Pivot<ISparseMatrixElement> findPivot(ISparseMatrix matrix, int eliminationStep, int max) {
    	assert(matrix != null);
        assert(eliminationStep >= 0);
        assert(max > 0);

        // No pivot possible if we're already eliminating outside of our bounds
        if (eliminationStep > max)
            return Pivot.empty();

        // Fix the search limit to allow our strategies to work
        for(MarkowitzSearchStrategy strategy : this._strategies){
            Pivot<ISparseMatrixElement> chosen = strategy.findPivot(this, matrix, eliminationStep, max);
            if (chosen.info != PivotInfo.None)
                return chosen;
        }
        return Pivot.empty();
    }
    /**
     * Gets the Markowitz row counts.
     */
    public int rowCount(int row) {
    	return this._markowitzRow[row];
    }
    /**
     * Gets the Markowitz column counts.
     * 
     */
    public int columnCount(int column) {
    	return this._markowitzColumn[column];
    }
    /**
     * Gets the Markowitz products.
     */
    public int product(int index) {
    	return this._markowitzProduct[index];
    }
	
	public double getAbsolutePivotThreshold() {
		return _absolutePivotThreshold;
	}
	
	public void setAbsolutePivotThreshold(double absolutePivotThreshold) {
		this._absolutePivotThreshold = absolutePivotThreshold;
	}
	
	public double getRelativePivotThreshold() {
		return _relativePivotThreshold;
	}
	
	public void setRelativePivotThreshold(double relativePivotThreshold) {
		this._relativePivotThreshold = relativePivotThreshold;
	}
	
	public int getSingletons() {
		return this._singletons;
	}
	
	public void setSingletons(int singletons) {
		this._singletons = singletons;
	}

}
