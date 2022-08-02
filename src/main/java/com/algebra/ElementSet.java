package com.algebra;

import java.util.ArrayList;
import java.util.List;

import com.algebra.matrix.MatrixLocation;
import com.algebra.solve.lu.sparsesolver.ISparseSolver;
/**
 * A set of matrix and right-hand-side vector elements
 * 
 * @author avornyo
 *
 * @param <T> The base type
 */
public class ElementSet {

	private final List<IElement> _elements;

	/**
	 * Initializes a new instance of the ElementSet
	 * 
	 * @param solver The solver
	 * @param matrixPins The Y-matrix indices
	 * @param rhsPins The right-hand side vector indices
	 */
	public ElementSet(ISparseSolver solver, MatrixLocation[] matrixPins, int[] rhsPins) {		
		assert(solver != null);

        // Allocate memory for all the elements
        int length = (rhsPins != null ? rhsPins.length: 0) + (matrixPins != null? matrixPins.length: 0);
        _elements = new ArrayList<IElement>(length);
        //int offset = 0;

        if (matrixPins != null){
            for (int i = 0; i < matrixPins.length; i++) {
                _elements.add((IElement) solver.getElement(matrixPins[i]));
            }
            //offset = matrixPins.length;
        }

        if (rhsPins != null){
            for (int i = 0; i < rhsPins.length; i++) {
            	 _elements.add((IElement) solver.getElement(rhsPins[i]));
            }
        }
	}
	
	/**
	 * Initializes a new instance of the ElementSet
	 * 
	 * @param solver The solver
	 * @param matrixPins The Y-matrix indices
	 */
	public ElementSet(ISparseSolver solver, MatrixLocation[] matrixPins) {
		this(solver, matrixPins, null);
    }

	/**
	 * Adds the specified values to each element. First come
     * the matrix elements, then the RHS vector elements.
     * 
	 * @param values The values to be added
	 */
	public void add(double ...values){
        if (values == null)
            return;
        for (int i = 0; i < values.length; i++) {
        	IElement element = getElements().get(i);
            element.add(values[i]);
        }
    }
	
	/**
	 * Subtracts the specified values. First come
     * the matrix elements, then the RHS vector elements.
     * 
	 * @param values The values to be subtracted
	 */
	public void subtract(Double ...values){
        if (values == null)
            return;

        for (int i = 0; i < values.length; i++) {
        	IElement element = getElements().get(i);
        	element.subtract(values[i]);
        }
    }
    
	public List<IElement> getElements() {
		return _elements;
	}
    
    @Override
    public String toString() {
    	return String.format("ElementSet (%x)", _elements.size());
    }
}
