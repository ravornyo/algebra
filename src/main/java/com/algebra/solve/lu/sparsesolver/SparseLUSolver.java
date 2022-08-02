package com.algebra.solve.lu.sparsesolver;

import com.algebra.diagnostics.AlgebraException;
import com.algebra.matrix.MatrixLocation;
import com.algebra.matrix.sparsematrix.ISparseMatrix;
import com.algebra.matrix.sparsematrix.ISparseMatrixElement;
import com.algebra.matrix.sparsematrix.SparseMatrix;
import com.algebra.messages.Messages;
import com.algebra.solve.Pivot;
import com.algebra.solve.PivotInfo;
import com.algebra.solve.PivotingSolver;
import com.algebra.solve.PreconditioningMethod;
import com.algebra.solve.lu.sparsesolver.pivoting.Markowitz;
import com.algebra.vector.IVector;
import com.algebra.vector.sparsevector.ISparseVector;
import com.algebra.vector.sparsevector.ISparseVectorElement;
import com.algebra.vector.sparsevector.SparseVector;

public class SparseLUSolver extends PivotingSolver<ISparseMatrix, ISparseVector> implements ISparsePivotingSolver{
	/**
	 * Fill-ins are elements that were auto-generated as a consequence
     * of the solver trying to solve the matrix. To save memory, this
     * number should remain small.
	 */
	private int _fillins;
	/**
	 * The pivoting strategy.
	 */
	public Markowitz _parameters;
	
	private double[] _intermediate;
	
	public SparseLUSolver() {
		super(new SparseMatrix(), new SparseVector());
		setParameters(new Markowitz());
        setFillins(0);
	}
	
	@Override
	public void precondition(PreconditioningMethod<ISparseMatrix, ISparseVector> method){
		SparseReorderedMatrix reorderedMatrix = new SparseReorderedMatrix(this);
		SparseReorderedVector reorderedVector = new SparseReorderedVector(this);
        method.apply(reorderedMatrix, reorderedVector);
    }

	@Override
    public boolean factor() {
        setFactored(false);
        int order = size() - getDegeneracy();
        for (int step = 1; step <= order; step++) {
            ISparseMatrixElement pivot = getMatrix().findDiagonalElement(step);

            // We don't consult the pivoting strategy, we just need to know if we can eliminate this row
            if (pivot == null || Math.abs(pivot.getValue()) == 0.0)
                return false;
            eliminate(getMatrix().findDiagonalElement(step));
        }
        setFactored(true);
        return true;
    }

    @Override
    public int orderAndFactor() throws AlgebraException {
        setFactored(false);
        int step = 1;
        int order = size() - getDegeneracy();
        int max = size() - getPivotSearchReduction();

        if (!needsReordering()) {
            // Matrix has been factored before, and reordering is not required
            for (step = 1; step <= order; step++) {
                ISparseMatrixElement pivot = getMatrix().findDiagonalElement(step);
                if (getParameters().isValidPivot(pivot, max))
                    eliminate(pivot);
                else {
                    setNeedsReordering(true);
                    break;
                }
            }

            if (!needsReordering()) {
                setFactored(true);
                return order;
            }
        }

        // Setup the strategy for some real kick-ass pivoting action
        getParameters().setup(getMatrix(), getVector(), step, max);

        for (; step <= order; step++) {
            Pivot<ISparseMatrixElement> pivot;
            if (step <= max)
                pivot = getParameters().findPivot(getMatrix(), step, max);
            else {
                ISparseMatrixElement elt = getMatrix().findDiagonalElement(step);
                pivot = new Pivot<ISparseMatrixElement>(elt, elt != null ? PivotInfo.Good : PivotInfo.None);
            }
            if (pivot.info.equals(PivotInfo.None))
                return step - 1;
            else if (pivot.info.equals(PivotInfo.Bad)) {
                MatrixLocation loc = internalToExternal(new MatrixLocation(step, step));
                throw new AlgebraException(String.format(Messages.getString("Algebra.BadlyConditioned"), loc.row, loc.column));
            }
            movePivot(pivot.element, step);
            eliminate(pivot.element);
        }

        setFactored(true);
        setNeedsReordering(false);
        return order;
    }

    public ISparseMatrixElement findDiagonalElement(int index) {
        assert(index >= 0);
        
        if (index > size())
            return null;
        int row = getRow().get(index);
        int column = getColumn().get(index);
        return getMatrix().findElement(new MatrixLocation(row, column));
    }
    
    @Override
    public ISparseMatrixElement findElement(MatrixLocation location) {
    	return getMatrix().findElement(externalToInternal(location));
    }

    @Override
    public ISparseVectorElement findElement(int row) {
    	return getVector().findElement(getRow().get(row));
    }

    @Override
    public ISparseMatrixElement getElement(MatrixLocation location) {
        location = externalToInternal(location);
        ISparseMatrixElement elt = getMatrix().getElement(location);

        // If we created a new row or column, let's move to the front
        // to keep the same equations that are linearly dependent
        if (getDegeneracy() > 0 && size() - getDegeneracy() > 0){
            if (location.getRow() == size())
                swapRows(size(), size() - getDegeneracy());
            if (location.getColumn() == size())
                swapColumns(size(), size() - getDegeneracy());
        }
        return elt;
    }

    @Override
    public boolean removeElement(MatrixLocation location){
        location = externalToInternal(location);
        return getMatrix().removeElement(location);
    }

    @Override
    public ISparseVectorElement getElement(int row) {
        assert(row >= 0);
        row = getRow().get(row);
        ISparseVectorElement elt = getVector().getElement(row);

        // If we created a new row, let's move it back to still have the same equations that are considered linearly dependent
        if (getDegeneracy() > 0){
            if (row == size())
                swapRows(size(), size() - getDegeneracy());
        }
        return elt;
    }

    @Override
    public boolean removeElement(int row) {
        row = getRow().get(row);
        return getVector().removeElement(row);
    }
    /**
     * Moves a chosen pivot to the diagonal.
     * 
     * @param pivot The pivot element.
     * @param step The current step of factoring
     */
    protected void movePivot(ISparseMatrixElement pivot, int step) {
        assert(pivot != null);
        getParameters().movePivot(getMatrix(), getVector(), pivot, step);

        // Move the pivot in the matrix
        swapRows(pivot.getRow(), step);
        swapColumns(pivot.getColumn(), step);

        // Update the pivoting strategy
        getParameters().update(getMatrix(), pivot, size() - getPivotSearchReduction());
    }
    /**
     * Creates a fillin. The fillin is an element that appeared as a by-product
     * of elimination/factoring the matrix.
     * 
     * @param location The location
     * @return The created fillin element.
     */
    protected ISparseMatrixElement createFillin(MatrixLocation location) {
        ISparseMatrixElement result = (ISparseMatrixElement)getMatrix().getElement(location);
        getParameters().createFillin(getMatrix(), result);
        setFillins(getFillins() + 1);
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        setFillins(0);
        setPivotSearchReduction(0);
        getParameters().clear();
    }
    
	/**
	 * @return the fillins
	 */
	public int getFillins() {
		return _fillins;
	}

	/**
	 * @param fillins the fillins to set
	 */
	public void setFillins(int _fillins) {
		this._fillins = _fillins;
	}

	/**
	 * @return the parameters
	 */
	public Markowitz getParameters() {
		return _parameters;
	}

	/**
	 * @param _parameters the _parameters to set
	 */
	public void setParameters(Markowitz _parameters) {
		this._parameters = _parameters;
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
        int order = size - getDegeneracy();

        // Scramble
        ISparseVectorElement rhsElement = getVector().getFirstInVector();
        int index = 0;
        while (rhsElement != null && rhsElement.getIndex() <= order){
            while (index < rhsElement.getIndex()) {
                _intermediate[index++] = 0.0;
            }
            _intermediate[index++] = rhsElement.getValue();
            rhsElement = rhsElement.getBelow();
        }
        while (index <= order)
            _intermediate[index++] = 0.0;
        while (index <= size) {
            _intermediate[index] = solution.get(getColumn().reverse(index));
            index++;
        }

        // Forward substitution
        for (int i = 1; i <= order; i++) {
            double temp = _intermediate[i];
            if (temp != 0.0) {
                ISparseMatrixElement pivot = getMatrix().findDiagonalElement(i);
                temp *= pivot.getValue();
                _intermediate[i] = temp;
                ISparseMatrixElement element = pivot.getBelow();
                while (element != null && element.getRow() <= order)
                {
                    _intermediate[element.getRow()] -= temp * element.getValue();
                    element = element.getBelow();
                }
            }
        }

        // Backward substitution
        for (int i = order; i > 0; i--) {
            double temp = _intermediate[i];
            ISparseMatrixElement pivot = getMatrix().findDiagonalElement(i);
            ISparseMatrixElement element = pivot.getRight();
            while (element != null) {
                temp -= element.getValue() * _intermediate[element.getColumn()];
                element = element.getRight();
            }
            _intermediate[i] = temp;
        }

        // Unscramble
        getColumn().unscramble(_intermediate, solution);
	}

	@Override
	public void solveTransposed(IVector solution) throws AlgebraException {
		assert(solution != null);
		
		if (!isFactored())
            throw new AlgebraException(String.format(Messages.getString("Algebra_SolverNotFactored")));
        if (solution.length() != size())
        	throw new IllegalArgumentException(String.format(Messages.getString("Algebra_VectorLengthMismatch"), solution.length(), size()));

        if (_intermediate == null || _intermediate.length != size() + 1)
            _intermediate = new double[size() + 1];
        
        int order = size() - getDegeneracy();

        // Scramble
        ISparseVectorElement rhsElement = getVector().getFirstInVector();
        for (int i = 0; i <= order; i++)
            _intermediate[i] = 0.0;
        while (rhsElement != null && rhsElement.getIndex() <= order){
            int newIndex = getColumn().get(getRow().reverse(rhsElement.getIndex()));
            _intermediate[newIndex] = rhsElement.getValue();
            rhsElement = rhsElement.getBelow();
        }

        // Forward elimination
        for (int i = 1; i <= order; i++) {
            double temp = _intermediate[i];
            if (temp != 0.0) {
                ISparseMatrixElement element = getMatrix().findDiagonalElement(i).getRight();
                while (element != null && element.getColumn() <= order) {
                    _intermediate[element.getColumn()] -= temp * element.getValue();
                    element = element.getRight();
                }
            }
        }

        // Backward substitution
        for (int i = order; i > 0; i--) {
            double temp = _intermediate[i];
            ISparseMatrixElement pivot = getMatrix().findDiagonalElement(i);
            ISparseMatrixElement element = pivot.getBelow();
            while (element != null && element.getRow() <= order) {
                temp -= _intermediate[element.getRow()] * element.getValue();
                element = element.getBelow();
            }
            _intermediate[i] = temp * pivot.getValue();
        }

        // Unscramble
        getRow().unscramble(_intermediate, solution);
	}

	protected void eliminate(ISparseMatrixElement pivot) {
		 // Test for zero pivot
        if (pivot == null || pivot.getValue() == 0.0)
        	throw new IllegalArgumentException(String.format(Messages.getString("Algebra_InvalidPivot"), pivot.getRow()));

        pivot.setValue(1.0 / pivot.getValue());

        ISparseMatrixElement upper = pivot.getRight();
        while (upper != null) {
            // Calculate upper triangular element
            // upper = upper / pivot
            upper.setValue(upper.getValue() * pivot.getValue());

            ISparseMatrixElement sub = upper.getBelow();
            ISparseMatrixElement lower = pivot.getBelow();
            while (lower != null) {
                int row = lower.getRow();

                // Find element in row that lines up with the current lower triangular element
                while (sub != null && sub.getRow() < row)
                    sub = sub.getBelow();

                // Test to see if the desired element was not found, if not, create fill-in
                if (sub == null || sub.getRow() > row)
                    sub = createFillin(new MatrixLocation(row, upper.getColumn()));

                // element -= upper * lower
                sub.setValue(sub.getValue() - upper.getValue() * lower.getValue());
                sub = sub.getBelow();
                lower = lower.getBelow();
            }
            upper = upper.getRight();
        }
	}

//	private double getValueOrDefault(IElement element) {
//		return element.getValue();
//	}

}
