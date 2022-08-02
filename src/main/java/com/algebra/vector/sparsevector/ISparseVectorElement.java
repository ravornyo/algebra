package com.algebra.vector.sparsevector;

import com.algebra.IElement;

/**
 * A vector element for an ISparseVector. This element has links
 * to the next and previous elements.
 * 
 * @author avornyo
 *
 * @param  The base type
 */
public interface ISparseVectorElement extends IElement{
	/**
	 * Gets the index in the vector.
	 * 
	 * @return The index
	 */
	int getIndex();
	/**
	 * Sets the index in the vector.
	 * 
	 * @param index The index
	 */
	void setIndex(int index);
	/**
	 * Gets the nearest ISparseVectorElement above this one.
	 * 
	 * @return  The vector element.
	 */
	ISparseVectorElement getAbove();
	/**
	 * Sets the nearest ISparseVectorElement above this one.
	 * 
	 * @param above  The vector element.
	 */
	void setAbove(ISparseVectorElement above);
	/**
	 * Gets the nearest ISparseVectorElement below this one.
	 * 
	 * @return  The vector element.
	 */
	ISparseVectorElement getBelow();
	/**
	 * Sets the nearest ISparseVectorElement below this one.
	 * 
	 * @param below  The vector element.
	 */
	void setBelow(ISparseVectorElement below);
	/**
	 * Gets the previous ISparseVectorElement before this one.
	 * 
	 * @return  The vector element.
	 */
	ISparseVectorElement getPreviousInVector();
	/**
	 * Sets the previous ISparseVectorElement after this one.
	 * 
	 * @param previousInVector The vector element.
	 */
	void setPreviousInVector(ISparseVectorElement previousInVector);
	/**
	 * Gets the next ISparseVectorElement after this one.
	 * 
	 * @return  The vector element.
	 */
	ISparseVectorElement getNextInVector();
	/**
	 * Sets the next ISparseVectorElement after this one.
	 * 
	 * @param nextInVector  The vector element.
	 */
	void setNextInVector(ISparseVectorElement nextInVector);
}
