package com.algebra.solve;

import java.util.Arrays;

import com.algebra.messages.Messages;
import com.algebra.vector.IVector;

public class Translation {
	private static final int INITIAL_SIZE = 4;
	private static final float EXPANSION_FACTOR = 1.5f;
	
    private int[] _extToInt;
    private int[] _intToExt;
    private int _allocated;
    /**
    * The length of the translation vector.
    */
    public int _length;
    /**
    * Initializes a new instance of the Translation class.
    */
    public Translation(int size) {
        assert(size >= 0);

        _extToInt = new int[size + 1];
        _intToExt = new int[size + 1];
        for (int i = 1; i <= size; i++) {
            _extToInt[i] = i;
            _intToExt[i] = i;
        }
        _allocated = size;
    }
    /**
    * Initializes a new instance of the Translation class.
    */
    public Translation() {
    	this(Translation.INITIAL_SIZE);
    }
    /**
     * Gets the internal index from an external index.
     * 
     * @param index The external index.
     * @return The internal index.
     */
    public int get(int index) {
    	assert(index >= 0);

        // Zero is mapped to zero
        if (index == 0)
            return 0;
        if (index > _allocated)
            this.expandTranslation(index);
        return _extToInt[index];
    }
    /**
     * Gets the external index from an internal index.
     * 
     * @param index The internal index.
     * @return The external index.
     */
    public int reverse(int index){
    	assert(index >= 0);
    	
        if (index == 0)
            return 0;
        if (index > _allocated)
            this.expandTranslation(index);
        return _intToExt[index];
    }
    /**
     * Swaps two (internal) indices, such that the external indices
     * 
     * @param index1 First index.
     * @param index2 Second index.
     */
    public void swap(int index1, int index2){
    	assert(index1 >= 0);
    	assert(index2 >= 0);

        if (index1 > _length || index2 > _length)
            this.expandTranslation(Math.max(index1, index2));

        // Get the matching external indices
        int tmp = _intToExt[index1];
        _intToExt[index1] = _intToExt[index2];
        _intToExt[index2] = tmp;

        // Update the external indices
        _extToInt[_intToExt[index1]] = index1;
        _extToInt[_intToExt[index2]] = index2;
    }
    /**
     * Scramble a vector according to the map.
     * 
     * @param  The value type of the vector.
     * @param source The source vector
     * @param target The target vector.
     */
    public  void scramble(IVector source, IVector target){
        assert(source != null);
        assert(target != null);
        
        if (source.length() != target.length())
            throw new IllegalArgumentException(String.format(Messages.getString("Algebra_VectorLengthMismatch"), target.length(), source.length()));

        // Expand translation vectors if necessary
        if (_allocated < source.length() || _allocated < target.length())
            expandTranslation(Math.max(source.length(), target.length()));

        for (int i = 1; i < _extToInt.length; i++)
            target.set(_extToInt[i], source.get(i));
    }
    /**
     * Unscramble a vector. The first index of the array is ignored.
     * 
     * @param  The value type of the vector.
     * @param source The source vector
     * @param target The target vector.
     */
    public  void unscramble(double[] source, IVector target) {
    	assert(source != null);
        assert(target != null);
        
        if (source.length != target.length() + 1)
        	throw new IllegalArgumentException(String.format(Messages.getString("Algebra_VectorLengthMismatch"), target.length(), source.length - 1));

        // Expand translation vectors if necessary
        if (_allocated < source.length || _allocated < target.length())
            expandTranslation(Math.max(source.length - 1, target.length()));

        for (int i = 1; i < source.length; i++)
            target.set(_intToExt[i], source[i]);
    }

    /**
    * Clears all translations.
    */
    public void clear() {
        int size = Translation.INITIAL_SIZE;
        _extToInt = new int[size + 1];
        _intToExt = new int[size + 1];
        for (int i = 1; i <= size; i++){
            _extToInt[i] = i;
            _intToExt[i] = i;
        }
        _allocated = size;
    }

    private void expandTranslation(int newLength) {
        // No need to reallocate vector
        if (newLength <= _allocated) {
            _length = newLength;
            return;
        }

        // Reallocate
        int oldAllocated = _allocated;
        _allocated = Math.max(newLength, (int)(_allocated * Translation.EXPANSION_FACTOR));

        _extToInt = Arrays.copyOf(_extToInt, _allocated + 1);
        _intToExt = Arrays.copyOf(_intToExt, _allocated + 1);
        for (int i = oldAllocated + 1; i <= _allocated; i++){
            _extToInt[i] = i;
            _intToExt[i] = i;
        }
        _length = newLength;
    }
}
