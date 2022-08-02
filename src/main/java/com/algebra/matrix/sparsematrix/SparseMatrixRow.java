package com.algebra.matrix.sparsematrix;

import com.algebra.matrix.MatrixLocation;

public class SparseMatrixRow {
	private ISparseMatrixElement _firstInRow;
	private ISparseMatrixElement _lastInRow;
	
	/**
	 * Gets the first element in the row
	 * 
	 * @return The first element in the colum
	 */
	public ISparseMatrixElement getFirstInRow() {
		return _firstInRow;
	}
	/**
	 * Sets the first element in the row
	 * 
	 * @param firstInRow The first element
	 */
	private void setFirstInRow(ISparseMatrixElement firstInRow) {
		this._firstInRow = firstInRow;
	}
	/**
	 * Gets the last element in the row
	 * 
	 * @return The last element in the colum
	 */
	public ISparseMatrixElement getLastInRow() {
		return _lastInRow;
	}
	/**
	 * Sets the first element in the row
	 * 
	 * @param lastInRow The last element
	 */
	private void setLastInRow(ISparseMatrixElement lastInRow) {
		this._lastInRow = lastInRow;
	}
	/**
	 * Gets an element in the row, or creates it if it doesn't exist yet.
	 * 
	 * @param location The location of the element
	 * @return The found or created element.
	 */
	public ISparseMatrixElement createOrGetElement(MatrixLocation location) {
		SparseMatrixElement result = null;
		ISparseMatrixElement element = getFirstInRow();
		ISparseMatrixElement lastElement = null;
        while (element != null){
            if (element.getColumn() > location.getColumn())
                break;
            if (element.getColumn() == location.getColumn()){
                // Found the element
                return element;
            }

            lastElement = element;
            element = element.getRight();
        }

        // Create a new element
        result = new SparseMatrixElement(location);

        // Update links for last element
        if (lastElement == null) {
            setFirstInRow(result);
        } else {
            lastElement.setRight(result);
        }
        result.setLeft(lastElement);

        // Update links for next element
        if (element == null) {
            setLastInRow(result);
        } else {
            element.setLeft(result);
        }
        result.setRight(element);
        
        return result;
	}
	/**
	 * Find an element in the row without creating it.
	 * 
	 * @param column The column index
	 * @return The element at the specified column, or null if the element doesn't exist.
	 */
    public ISparseMatrixElement find(int column) {
    	ISparseMatrixElement element = getFirstInRow();
        while (element != null){
            if (element.getColumn() == column)
                return element;
            if (element.getColumn() > column)
                return null;
            element = element.getRight();
        }
        return null;
    }
    /**
     * Remove an element from the row
     * 
     * @param element The element to be removed
     */
    public void remove(ISparseMatrixElement element){
        if (element.getLeft() == null) {
            setFirstInRow(element.getRight());
        } else {
            element.getLeft().setRight(element.getRight());
        }
        if (element.getRight() == null) {
            setLastInRow(element.getLeft());
        } else {
            element.getRight().setLeft(element.getLeft());
        }
    }
    /**
     * Clears all matrix elements in the row.
     */
    public void clear() {
        setFirstInRow(null);
        setLastInRow(null);
    }
    
	/**
	 * Swap two elements in the row
	 * 
	 * @param first The first matrix element.
	 * @param second The second matrix element.
	 * @param columnFirst The first column.
	 * @param columnSecond The second column.
	 */
	public void swap(ISparseMatrixElement first, ISparseMatrixElement second, int columnFirst, int columnSecond) {
		if (first == null && second == null) {
			throw new IllegalArgumentException();
		}

		if (first == null) {
			// Do we need to move the element?
			if (second.getLeft() == null || second.getLeft().getColumn() < columnFirst) {
				second.setColumn(columnFirst);
				return;
			}
			// Move the element back
			ISparseMatrixElement element = second.getLeft();
			remove(second);
			while (element.getLeft() != null && element.getLeft().getColumn() > columnFirst) {
				element = element.getLeft();
			}
			
			// We now have the first element below the insertion point
			if (element.getLeft() == null) {
				setFirstInRow(second);
			} else {
				element.getLeft().setRight(second);
			}
			second.setLeft(element.getLeft());
			element.setLeft(second);
			second.setRight(element);
			second.setColumn(columnFirst);

		} else if (second == null) {
			// Do we need to move the element?
			if (first.getRight() == null || first.getRight().getColumn() > columnSecond) {
				first.setColumn(columnSecond);
				return;
			}

			// Move the element forward
			ISparseMatrixElement element = first.getRight();
			remove(first);
			while (element.getRight() != null && element.getRight().getColumn() < columnSecond) {
				element = element.getRight();
			}

			// We now have the first element above the insertion point
			if (element.getRight() == null) {
				setLastInRow(first);
			} else {
				element.getRight().setLeft(first);
			}
			first.setRight(element.getRight());
			element.setRight(first);
			first.setLeft(element);
			first.setColumn(columnSecond);

		} else {
			// Are they adjacent or not?
			if (first.getRight() == second) {
				// Correct surrounding links
				if (first.getLeft() == null)
					setFirstInRow(second);
				else
					first.getLeft().setRight(second);
				if (second.getRight() == null)
					setLastInRow(first);
				else
					second.getRight().setLeft(first);

				// Correct element links
				first.setRight(second.getRight());
				second.setLeft(first.getLeft());
				first.setLeft(second);
				second.setRight(first);
				first.setColumn(columnSecond);
				second.setColumn(columnFirst);

			} else {
				// Swap surrounding links
				if (first.getLeft() == null) {
					setFirstInRow(second);
				} else {
					first.getLeft().setRight(second);
				}
				first.getRight().setLeft(second);
				if (second.getRight() == null) {
					setLastInRow(first);
				} else {
					second.getRight().setLeft(first);
				}
				second.getLeft().setRight(first);

				// Swap element links
				ISparseMatrixElement element = first.getLeft();
				first.setLeft(second.getLeft());
				second.setLeft(element);

				element = first.getRight();
				first.setRight(second.getRight());
				second.setRight(element);
				first.setColumn(columnSecond);
				second.setColumn(columnFirst);
			}
		}
	}
  
}
