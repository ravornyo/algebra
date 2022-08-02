package com.algebra.matrix.sparsematrix;

/**
 * A class that keeps track of a linked list of matrix elements for a column.
 * 
 * @author avornyo
 *
 */
public class SparseMatrixColumn {
	private ISparseMatrixElement _firstInColumn;
	private ISparseMatrixElement _lastInColumn;
	/**
	 * Insert an element in the column. This method assumes an element 
	 * does not exist at its indices!
	 * 
	 * @param newElement The new element to insert
	 */
	public void insert(ISparseMatrixElement newElement) {
		assert(newElement != null);
		
		int row = newElement.getRow();
		ISparseMatrixElement element = getFirstInColumn(), lastElement = null;
        while (element != null){
            if (element.getRow() > row)
                break;
            lastElement = element;
            element = element.getBelow();
        }

        // Update links for last element
        if (lastElement == null) {
            setFirstInColumn(newElement);
        } else {
            lastElement.setBelow(newElement);
        }
        newElement.setAbove(lastElement);

        // Update links for next element
        if (element == null) {
            setLastInColumn(newElement);
        } else {
            element.setAbove(newElement);
        }
        newElement.setBelow(element);
	}
	/**
	 * Gets the first element in the column
	 * 
	 * @return The first element in the colum
	 */
	public ISparseMatrixElement getFirstInColumn() {
		return _firstInColumn;
	}
	/**
	 * Sets the first element in the column
	 * 
	 * @param firstInColumn The first element
	 */
	private void setFirstInColumn(ISparseMatrixElement firstInColumn) {
		this._firstInColumn = firstInColumn;
	}
	/**
	 * Gets the last element in the column
	 * 
	 * @return The last element in the colum
	 */
	public ISparseMatrixElement getLastInColumn() {
		return _lastInColumn;
	}
	/**
	 * Sets the first element in the column
	 * 
	 * @param lastInColumn The last element
	 */
	private void setLastInColumn(ISparseMatrixElement lastInColumn) {
		this._lastInColumn = lastInColumn;
	}
	
    /**
     * Remove an element from the row
     * 
     * @param element The element to be removed
     */
    public void remove(ISparseMatrixElement element){
        if (element.getAbove() == null) {
            setFirstInColumn(element.getBelow());
        } else {
            element.getAbove().setBelow(element.getBelow());
        }
        if (element.getBelow() == null) {
            setLastInColumn(element.getAbove());
        } else {
            element.getBelow().setAbove(element.getAbove());
        }
    }
    
    /**
     * Clears all matrix elements in the column.
     */
    public void clear() {
        setFirstInColumn(null);
        setLastInColumn(null);
    }
    
	/**
	 * Swap two elements in the column
	 * 
	 * @param first The first matrix element.
	 * @param second The second matrix element.
	 * @param rowFirst The first row.
	 * @param rowSecond The second row.
	 */
	public void swap(ISparseMatrixElement first, ISparseMatrixElement second, int rowFirst, int rowSecond) {
		if (first == null && second == null) {
			throw new IllegalArgumentException();
		}

		if (first == null) {
			// Do we need to move the element?
			if (second.getAbove() == null || second.getAbove().getRow() < rowFirst) {
				second.setRow(rowFirst);
				return;
			}
			// Move the element back
			ISparseMatrixElement element = second.getAbove();
			remove(second);
			while (element.getAbove() != null && element.getAbove().getRow() > rowFirst) {
				element = element.getAbove();
			}

			// We now have the first element below the insertion point
			if (element.getAbove() == null) {
				setFirstInColumn(second);
			} else {
				element.getAbove().setBelow(second);
			}
			second.setAbove(element.getAbove());
			element.setAbove(second);
			second.setBelow(element);
			second.setRow(rowFirst);

		} else if (second == null) {
			// Do we need to move the element?
			if (first.getBelow() == null || first.getBelow().getRow() > rowSecond) {
				first.setRow(rowSecond);
				return;
			}

			// Move the element forward
			ISparseMatrixElement element = first.getBelow();
			remove(first);
			while (element.getBelow() != null && element.getBelow().getRow() < rowSecond) {
				element = element.getBelow();
			}

			// We now have the first element above the insertion point
			if (element.getBelow() == null) {
				setLastInColumn(first);
			} else {
				element.getBelow().setAbove(first);
			}
			first.setBelow(element.getBelow());
			element.setBelow(first);
			first.setAbove(element);
			first.setRow(rowSecond);

		} else {
			// Are they adjacent or not?
			if (first.getBelow() == second) {
				// Correct surrounding links
				if (first.getAbove() == null)
					setFirstInColumn(second);
				else
					first.getAbove().setBelow(second);
				if (second.getBelow() == null)
					setLastInColumn(first);
				else
					second.getBelow().setAbove(first);

				// Correct element links
				first.setBelow(second.getBelow());
				second.setAbove(first.getAbove());
				first.setAbove(second);
				second.setBelow(first);
				first.setRow(rowSecond);
				second.setRow(rowFirst);

			} else {
				// Swap surrounding links
				if (first.getAbove() == null) {
					setFirstInColumn(second);
				} else {
					first.getAbove().setBelow(second);
				}
				first.getBelow().setAbove(second);
				if (second.getBelow() == null) {
					setLastInColumn(first);
				} else {
					second.getBelow().setAbove(first);
				}
				second.getAbove().setBelow(first);

				// Swap element links
				ISparseMatrixElement element = first.getAbove();
				first.setAbove(second.getAbove());
				second.setAbove(element);

				element = first.getBelow();
				first.setBelow(second.getBelow());
				second.setBelow(element);
				first.setRow(rowSecond);
				second.setRow(rowFirst);
			}
		}
	}
}
