package com.algebra.solve.lu.densesolver;

import com.algebra.diagnostics.AlgebraException;
import com.algebra.matrix.IMatrix;
import com.algebra.matrix.MatrixLocation;
import com.algebra.matrix.densematrix.DenseMatrix;
import com.algebra.messages.Messages;
import com.algebra.solve.ISolver;
import com.algebra.solve.Pivot;
import com.algebra.solve.PivotInfo;
import com.algebra.solve.PivotingSolver;
import com.algebra.solve.PreconditioningMethod;
import com.algebra.vector.IVector;
import com.algebra.vector.densevector.DenseVector;

public class DenseLUSolver extends PivotingSolver<IMatrix, IVector> implements ISolver{

	private RookPivoting _parameters;
	private double[] _intermediate;
	
	public DenseLUSolver() {
		super(new DenseMatrix(), new DenseVector());
		
		setNeedsReordering(true);
        this._parameters = new RookPivoting();
	}
	
	public DenseLUSolver(int size) {
		super(new DenseMatrix(size), new DenseVector(size));
		
		setNeedsReordering(true);
        this._parameters = new RookPivoting();
	}

	@Override
	public void precondition(PreconditioningMethod<IMatrix, IVector> method) {
		DenseReorderedMatrix reorderedMatrix = new DenseReorderedMatrix(this);
		DenseReorderedVector reorderedVector = new DenseReorderedVector(this);
        method.apply(reorderedMatrix, reorderedVector);
	}

	@Override
	public int orderAndFactor() {
        int step = 1;
        int order = size() - getDegeneracy();
        int max = size() - getPivotSearchReduction();

        if (!needsReordering()){
            for (step = 1; step <= order; step++){
                if (getParameters().isValidPivot(getMatrix(), step, max))
                    eliminate(step, size());
                else {
                    setNeedsReordering(true);
                    break;
                }
            }

            if (!needsReordering()){
                setFactored(true);
                return order;
            }
        }

        for (; step <= order; step++) {
            Pivot<MatrixLocation> pivot = getParameters().findPivot(getMatrix(), step, max);
            if (pivot.info == PivotInfo.None)
                return step - 1;
            swapRows(pivot.element.getRow(), step);
            swapColumns(pivot.element.getColumn(), step);
            eliminate(step, size());
        }
        setFactored(true);
        setNeedsReordering(false);
        return order;
	}

	@Override
	public boolean factor() {
		return factor(size());
	}
	
	@Override
	public void solve(IVector solution) throws AlgebraException {
		solve(solution, size());
	}

	@Override
	public void solve(IVector solution, int size) throws AlgebraException {
		assert(solution != null);
		
		if (!isFactored())
            throw new AlgebraException(String.format(Messages.getString("Algebra_SolverNotFactored")));
        if (solution.length() != size)
        	throw new IllegalArgumentException(String.format(Messages.getString("Algebra_VectorLengthMismatch"), solution.length(), size));

        if (_intermediate == null || _intermediate.length != size + 1)
            _intermediate = new double[size + 1];
        //size = Math.Min(size, size);
        int order = Math.min(size, size - getDegeneracy());

        // Fill in the values from the solution for degenerate cases
        for (int i = order + 1; i <= size; i++)
            _intermediate[i] = solution.get(getColumn().reverse(i));

        // Forward substitution
        for (int i = 1; i <= order; i++) {
            _intermediate[i] = getVector().get(i);
            for (int j = 1; j < i; j++)
                _intermediate[i] -= getMatrix().get(i, j) * _intermediate[j];
        }

        // Backward substitution
        for (int i = order; i >= 1; i--) {
            for (int j = i + 1; j <= size; j++)
                _intermediate[i] -= getMatrix().get(i, j) * _intermediate[j];
            _intermediate[i] *= getMatrix().get(i, i);
        }

        getColumn().unscramble(_intermediate, solution);
	}

	@Override
	public void solveTransposed(IVector solution) throws AlgebraException {
		assert(solution != null);
		
		if (!isFactored())
            throw new AlgebraException(String.format(Messages.getString("Algebra_SolverNotFactored")));
        if (solution.length() != size())
        	throw new IllegalArgumentException(String.format(Messages.getString("Algebra_VectorLengthMismatch"), solution.length(), size()));

        int steps = size();
        if (_intermediate == null || _intermediate.length != size() + 1)
            _intermediate = new double[size() + 1];
        steps = Math.max(steps, size());

        // Scramble
        for (int i = 1; i <= steps; i++) {
            int newIndex = getColumn().get(getRow().reverse(i));
            _intermediate[newIndex] = getVector().get(i);
        }

        // Forward substitution
        for (int i = 1; i <= steps; i++) {
            for (int j = 1; j < i; j++)
                _intermediate[i] -= getMatrix().get(i, j) * getVector().get(j);
        }

        // Backward substitution
        _intermediate[steps] *= getMatrix().get(steps, steps);
        for (int i = steps - 1; i >= 1; i--) {
            for (int j = i + 1; j <= steps; j++)
                _intermediate[i] -= getMatrix().get(i, j) * _intermediate[j];
            _intermediate[i] *= getMatrix().get(i, i);
        }

        getRow().unscramble(_intermediate, solution);
	}
	/**
	 * Eliminates the submatrix right and below the pivot.
	 * 
	 * @param step The current elimination step.
	 * @param size The maximum row/column to be eliminated.
	 */
	protected void eliminate(int step, int size) {
		 double diagonal = getMatrix().get(step, step);
         if (diagonal == 0.0)
        	 throw new IllegalArgumentException(String.format(Messages.getString("Algebra_InvalidPivot"), step));
         diagonal = 1.0 / diagonal;
         getMatrix().set(step, step, diagonal);

         for (int r = step + 1; r <= size; r++) {
             double lead = getMatrix().get(r, step);
             if (lead == 0.0)
                 continue;
             lead *= diagonal;
             getMatrix().set(r, step, lead);

             for (int c = step + 1; c <= size; c++) {
            	 double temp = getMatrix().get(r, c) - (lead * getMatrix().get(step, c));
                 getMatrix().set(r, c, temp);
             }
         }
	}
	
	public boolean factor(int size) {
		int order = Math.min(size, size()) - getDegeneracy();
        for (int step = 1; step <= order; step++) {
            double pivot = getMatrix().get(step, step);
            if (pivot == 0.0)
                return false;
            eliminate(step, size);
        }
        setFactored(true);
        return true;
	}
	
	@Override
	public void clear() {
        super.clear();
        setPivotSearchReduction(0);
    }

	public RookPivoting getParameters() {
		return _parameters;
	}


}
