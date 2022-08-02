package com.algebra.vector.sparsevector;

import com.algebra.vector.IVector;

/**
 * Describes a vector that can be stepped through.
 * 
 * @author avornyo
 *
 * @param  The base type
 */
public interface ISparseVector extends IVector{
	/**
	 * Gets the number of elements in the vector.
	 * 
	 * @return The element count
	 */
    int getElementCount();
    /**
     * Gets the first ISparseVectorElement in the vector.
     * 
     * @return The first element in the vector.
     */
    ISparseVectorElement getFirstInVector();
    /**
     * Gets the last ISparseVectorElement in the vector.
     * 
     * @return The last element in the vector.
     */
    ISparseVectorElement getLastInVector();
    /**
     * Gets a vector element at the specified index. If
     * it doesn't exist, a new one is created.
     * 
     * @param index The index.
     * @return The vector element.
     */
    ISparseVectorElement getElement(int index);
    /**
     * Removes a vector element at the specified index
     * 
     * @param index The index.
     * @return true if the element was removed; otherwise, false
     */
    boolean removeElement(int index);
    /**
     * Finds a vector element at the specified index.
     * 
     * @param index The index
     * @return The vector element; otherwise null
     */
    ISparseVectorElement findElement(int index);
}
