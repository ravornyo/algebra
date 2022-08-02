package com.algebra.vector.densevector;

import java.util.Arrays;

import com.algebra.messages.Messages;
import com.algebra.vector.IVector;

/**
 * A vector with real values
 * 
 * The element at index 0 is considered a "trashcan" element under the hood, consistent to SparseMatrix.
 * This doesn't really make a difference for indexing the vector, but it does give different meanings to the length of
 * the vector.8
 * This vector does not automatically expand size if necessary. Under the hood it is basically just an array.
 * 
 * @author avornyo
 *
 * @param  The base type
 */
public class DenseVector implements IVector {

	private static final float EXPANSION_FACTOR = 1.5f;
    private static final int INITIAL_SIZE = 4;
    
	private int _length;
	private double[] _values;
	
	public DenseVector() {
        this._length = 0;
        this._values = new double[INITIAL_SIZE];
    }

    public DenseVector(int length) {
        assert(!(length < 0 && length > Integer.MAX_VALUE - 1));
        this._length = length;
        this._values = new double[length + 1];
    }
    
	@Override
	public int length() {
		return this._length;
	}

	@Override
	public double get(int index) {
		assert(index >= 0);
		
        if (index > length())
            return 0;
        return this._values[index];
	}

	@Override
	public void set(int index, double value) {
		assert(index >= 0);
		 if (index > length())
             expand(index);
         this._values[index] = value;
	}

	@Override
	public void swapElements(int index1, int index2) {
		assert(index1 >= 0);
		assert(index2 >= 0);
		
        if (index1 > length() || index2 > length())
            expand(Math.max(index1, index2));
        if (index1 == index2)
            return;
        double tmp = this._values[index1];
        _values[index1] = _values[index2];
        _values[index2] = tmp;
	}

	@Override
	public void copyTo(IVector target) {
		 assert(target != null);
         if (length() != target.length())
        	 throw new IllegalArgumentException(String.format(Messages.getString("Algebra_VectorLengthMismatch"), target.length(), length()));
         if (target == this)
             return;
         for (int i = 1; i <= length(); i++)
             target.set(i, get(i));
	}

	@Override
	public void reset() {
		for (int i = 0; i < _values.length; i++)
            _values[i] = 0;
	}

	@Override
	public void clear() {
		_values = new double[INITIAL_SIZE + 1];
        this._length = 0;
	}
	
	private void expand(int newSize) {
        this._length = newSize;
        if (newSize + 1 <= _values.length)
            return;
        newSize = Math.max(newSize, (int)(_values.length * EXPANSION_FACTOR));        
        this._values = Arrays.copyOf(this._values, newSize + 1);
    }
	
    @Override
    public String toString() {
    	return String.format("Dense vector (%x)", length());
    }

}
