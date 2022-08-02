package com.algebra.vector.sparsevector;

import com.algebra.messages.Messages;
import com.algebra.vector.IVector;

public class SparseVector implements ISparseVector {
	
	private int _length;
	private SparseVectorElement _firstInVector, _lastInVector;
    private final SparseVectorElement _trashCan;
	private int _elementCount;
	
	public SparseVector() {
		this(0);
	}
	
	public SparseVector(int length) {
		_length = length;
		_trashCan = new SparseVectorElement(0);
        _elementCount = 1;
	}

	@Override
	public int length() {
		return this._length;
	}

	@Override
	public double get(int index) {
		ISparseVectorElement element = findElement(index);
        if (element == null)
            return 0;
        return element.getValue();
	}

	@Override
	public void set(int index, double value) {
		ISparseVectorElement element = getElement(index);
        element.setValue(value);
	}

	@Override
	public void swapElements(int index1, int index2) {
		assert(index1 >= 0);
		assert(index2 >= 0);
		
        if (index1 == index2)
            return;
        if (index2 < index1){
            int tmp = index1;
            index1 = index2;
            index2 = tmp;
        }

        // Get the two elements
        ISparseVectorElement first = null, second = null;

        // Find first element
        ISparseVectorElement element = _firstInVector;
        while (element != null) {
            if (element.getIndex() == index1)
                first = element;
            if (element.getIndex() > index1)
                break;
            element = element.getNextInVector();
        }

        // Find second element
        while (element != null){
            if (element.getIndex() == index2)
                second = element;
            if (element.getIndex() > index2)
                break;
            element = element.getNextInVector();
        }

        // Swap these elements
        swap(first, second, index1, index2);
	}

	private void swap(ISparseVectorElement first, ISparseVectorElement second, int index1, int index2) {
		// Nothing to do
        if (first == null && second == null)
            return;

        // Swap the elements
        if (first == null) {
            // Do we need to move the element?
            if (second.getPreviousInVector() == null || second.getPreviousInVector().getIndex() < index1) {
                second.setIndex(index1);
                return;
            }

            // Move the element back
            ISparseVectorElement element = second.getPreviousInVector();
            remove(second);
            while (element.getPreviousInVector() != null && element.getPreviousInVector().getIndex() > index1)
                element = element.getPreviousInVector();

            // We now have the element below the insertion point
            if (element.getPreviousInVector() == null)
                _firstInVector = (SparseVectorElement) second;
            else
                element.getPreviousInVector().setNextInVector(second);
            second.setPreviousInVector(element.getPreviousInVector());
            element.setPreviousInVector(second);
            second.setNextInVector(element);
            second.setIndex(index1);
        }
        else if (second == null) {
            // Do we need to move the element?
            if (first.getNextInVector() == null || first.getNextInVector().getIndex() > index2){
                first.setIndex(index2);
                return;
            }

            // Move element forward
            ISparseVectorElement element = first.getNextInVector();
            remove(first);
            while (element.getNextInVector() != null && element.getNextInVector().getIndex() < index2)
                element = element.getNextInVector();

            // We now have the first element above the insertion point
            if (element.getNextInVector() == null)
                _lastInVector = (SparseVectorElement) first;
            else
                element.getNextInVector().setPreviousInVector(first);
            first.setNextInVector(element.getNextInVector());
            element.setNextInVector(first);
            first.setPreviousInVector(element);
            first.setIndex(index2);
        }
        else {
            // Are they adjacent or not?
            if (first.getNextInVector() == second) {
                // Correct surrounding links
                if (first.getPreviousInVector() == null)
                    _firstInVector = (SparseVectorElement) second;
                else
                    first.getPreviousInVector().setNextInVector(second);
                if (second.getNextInVector() == null)
                    _lastInVector = (SparseVectorElement) first;
                else
                    second.getNextInVector().setPreviousInVector(first);

                // Correct element links
                first.setNextInVector(second.getNextInVector());
                second.setPreviousInVector(first.getPreviousInVector());
                first.setPreviousInVector(second);
                second.setNextInVector(first);
                first.setIndex(index2);
                second.setIndex(index1);
            }
            else {
                // Swap surrounding links
                if (first.getPreviousInVector() == null)
                    _firstInVector = (SparseVectorElement) second;
                else
                    first.getPreviousInVector().setNextInVector(second);
                first.getNextInVector().setPreviousInVector(second);
                if (second.getNextInVector() == null)
                    _lastInVector = (SparseVectorElement) first;
                else
                    second.getNextInVector().setPreviousInVector(first);
                second.getPreviousInVector().setNextInVector(first);

                // Swap element links
                ISparseVectorElement element = first.getPreviousInVector();
                first.setPreviousInVector(second.getPreviousInVector());
                second.setPreviousInVector(element);

                element = first.getNextInVector();
                first.setNextInVector(second.getNextInVector());
                second.setNextInVector(element);

                first.setIndex(index2);
                second.setIndex(index1);
            }
        }	
	}
	
	private void remove(ISparseVectorElement element) {
        // Update surrounding links
        if (element.getPreviousInVector() == null)
            _firstInVector = (SparseVectorElement) element.getNextInVector();
        else
            element.getPreviousInVector().setNextInVector(element.getNextInVector());
        if (element.getNextInVector() == null)
            _lastInVector = (SparseVectorElement) element.getPreviousInVector();
        else
            element.getNextInVector().setPreviousInVector(element.getPreviousInVector());
        this._elementCount--;
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
		this._trashCan.setValue(0.0);
        ISparseVectorElement elt = _firstInVector;
        while (elt != null) {
            elt.setValue(0.0);
            elt = elt.getNextInVector();
        }
	}

	@Override
	public void clear() {
		this._trashCan.setValue(0.0);
		this._firstInVector = null;
		this._lastInVector = null;
        this._elementCount = 1;
        this._length = 0;
	}

	@Override
	public int getElementCount() {
		return this._elementCount;
	}

	@Override
	public ISparseVectorElement getFirstInVector() {
		return this._firstInVector;
	}

	@Override
	public ISparseVectorElement getLastInVector() {
		return this._lastInVector;
	}

	@Override
	public ISparseVectorElement getElement(int index) {
		assert(index >= 0);
		
        if (index == 0)
            return _trashCan;

        // Expand the vector if it is necessary
        if (index > this._length)
            this._length = index;

        SparseVectorElement element = _firstInVector;
		// Find the element
        SparseVectorElement lastElement = null;
        while (element != null) {
            if (element.getIndex() > index)
                break;
            if (element.getIndex() == index)
                return element;
            lastElement = element;
            element = (SparseVectorElement) element.getNextInVector();
        }

        // Create a new element
        SparseVectorElement result = new SparseVectorElement(index);

        // Update links for last element
        if (lastElement == null)
            _firstInVector = result;
        else
            lastElement.setNextInVector(result);
        result.setPreviousInVector(lastElement);

        // Update links for next element
        if (element == null)
            _lastInVector = result;
        else
            element.setPreviousInVector(result);
        result.setNextInVector(element);

        this._elementCount++;
        return result;
	}

	@Override
	public boolean removeElement(int index) {
		if (index < 1 || index > length())
            return false;

        // Find the element
        ISparseVectorElement element = _firstInVector;
        while (element != null) {
            if (element.getIndex() == index)
                break;
            if (element.getIndex() > index)
                return false;
            element = element.getNextInVector();
        }

        if (element == null)
            return false;
        remove(element);
        return true;
	}

	@Override
	public ISparseVectorElement findElement(int index) {
		assert(index >= 0);
        if (index > length())
            return null;
        if (index == 0)
            return _trashCan;

        // Find the element
        ISparseVectorElement element = _firstInVector;
        while (element != null)  {
            if (element.getIndex() == index)
                return element;
            if (element.getIndex() > index)
                return null;
            element = element.getNextInVector();
        }
        return null;
	}

}
