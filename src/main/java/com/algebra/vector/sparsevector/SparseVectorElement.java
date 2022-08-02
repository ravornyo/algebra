package com.algebra.vector.sparsevector;

import com.algebra.Element;

/**
 * A vector element for SparseVector
 * 
 * @author avornyo
 *
 * @param  The base type
 */
public class SparseVectorElement extends Element implements ISparseVectorElement {

	private int _index;
	private SparseVectorElement _nextInVector;
	private SparseVectorElement _previousInVector;

	SparseVectorElement(int index){
		setIndex(index);
		setValue(0.0);
	}

	@Override
	public int getIndex() {
		return _index;
	}

	@Override
	public void setIndex(int index) {
		_index = index;
	}

	@Override
	public ISparseVectorElement getAbove() {
		return _previousInVector;
	}
	
	@Override
	public void setAbove(ISparseVectorElement above) {
		this._nextInVector = (SparseVectorElement) above;
	}

	@Override
	public ISparseVectorElement getBelow() {
		return _nextInVector;
	}
	
	@Override
	public void setBelow(ISparseVectorElement below) {
		this._previousInVector = (SparseVectorElement) below;	
	}

	@Override
	public ISparseVectorElement getNextInVector() {
		return _nextInVector;
	}

	@Override
	public void setNextInVector(ISparseVectorElement nextInVector) {
		this._nextInVector = (SparseVectorElement) nextInVector;
	}

	@Override
	public ISparseVectorElement getPreviousInVector() {
		return _previousInVector;
	}

	@Override
	public void setPreviousInVector(ISparseVectorElement previousInVector) {
		this._previousInVector = (SparseVectorElement) previousInVector;
	}

}
