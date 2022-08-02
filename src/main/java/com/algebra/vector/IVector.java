package com.algebra.vector;

/**
 * A description of a vector.
 * 
 * @author avornyo
 *
 * @param <T> The base type
 */
public interface IVector {
	/**
	 * Gets the length of the vector.
	 * 
	 * @return Vector length
	 */
	int length();
	/**
	 * Gets the value at the specified index.
	 * 
	 * @param index The index
	 * @return The value
	 */
    double get(int index);
    /**
     * Sets the value at the specified index.
     * 
     * @param index
     * @param value
     */
    void set(int index, double value);
    /**
     * Swaps two elements in the vector.
     * 
     * @param index1 The first index.
     * @param index2 The second index.
     */
    void swapElements(int index1, int index2);
    /**
     * Copies the contents of the vector to another one.
     * 
     * @param target The target vector.
     */
    void copyTo(IVector target);
    /**
     * Resets all elements in the vector to their default value.
     */
    void reset();
    /**
     * Clears all elements in the vector. The size of the vector becomes 0.
     */
    void clear();
}
