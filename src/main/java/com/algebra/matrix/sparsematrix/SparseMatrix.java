package com.algebra.matrix.sparsematrix;

import java.util.ArrayList;
import java.util.List;

import com.algebra.matrix.MatrixLocation;

/**
 * A square matrix that uses a sparse storage method with doubly-linked elements.
 * 
 * The elements in row and column with index 0 are considered trashcan elements. They
 * should all map on the same element. The matrix automatically expands size if necessary.
 * 
 * @author avornyo
 *
 * @param  The base value type
 */
public class SparseMatrix implements ISparseMatrix {
    private static final int INITIAL_SIZE = 4;
    private static final float EXPANSION_FACTOR = 1.5f;
    
    private List<SparseMatrixRow> _rows;
    private List<SparseMatrixColumn> _columns;
    private List<ISparseMatrixElement> _diagonal;
    private SparseMatrixElement _trashCan;
    private int _allocatedSize;
    
	private int _elementCount;
	private int _size;
	/**
	 * Initializes a new instance of the SparseMatrix class
	 */
	public SparseMatrix() {
		 this(0);
	}
	/**
	 * Initializes a new instance of the SparseMatrix class
	 * 
	 * @param size The matrix size
	 */
	public SparseMatrix(int size) {
		assert(size >= 0);
		
		 _size = size;
         _allocatedSize = Math.max(INITIAL_SIZE, size);

         // Allocate rows
         _rows = new ArrayList<SparseMatrixRow>(_allocatedSize + 1);
         _rows.add(null);
         for (int i = 1; i <= _allocatedSize; i++) {
             _rows.add(new SparseMatrixRow());
         }

         // Allocate columns
         _columns = new ArrayList<SparseMatrixColumn>(_allocatedSize + 1);
         _columns.add(null);
         for (int i = 1; i <= _allocatedSize; i++) {
        	 _columns.add(new SparseMatrixColumn());
         }

         // Other
         _diagonal = new ArrayList<ISparseMatrixElement>(_allocatedSize + 1);
         for (int i = 0; i < _allocatedSize + 1; i++) {
        	 _diagonal.add(null);
         }
         _trashCan = new SparseMatrixElement(new MatrixLocation());
        _elementCount = 1;
	}
	/**
	 * Gets a pointer to the matrix element at the specified row and column. If
     * the element doesn't exist, it is created.
	 * 
	 * @param location
	 * @return The matrix element if it exists; otherwise null
	 */
	public ISparseMatrixElement getElement(MatrixLocation location){
		if (location.getRow() == 0 || location.getColumn() == 0)
            return _trashCan;

        // Expand our matrix if it is necessary!
        if (location.getRow() > getSize() || location.getColumn() > getSize()) {
            expand(Math.max(location.getRow(), location.getColumn()));
        }
        // Quick access to diagonals
        if (location.getRow() == location.getColumn()  && _diagonal.get(location.getRow()) != null) {
            return _diagonal.get(location.getRow());
        }
        ISparseMatrixElement element = _rows.get(location.getRow()).createOrGetElement(location);
        if (element != null){
            _elementCount++;
            _columns.get(location.getColumn()).insert(element);
            if (location.getRow() == location.getColumn()) {
            	_diagonal.set(location.getRow(), element);
            }
        }

        return element;
	}
	/**
	 * Removes a matrix element at the specified row and column. If the element
     * doesn't exist, this method returns false.
     * 
	 * @param location The matrix location
	 * @return
	 */
	public boolean removeElement(MatrixLocation location) {
        if (location.getRow() < 1 || location.getColumn() < 1) {
            return false;
        }
        if (location.getRow() > getSize() || location.getColumn() > getSize()) {
            return false;
        }
        // Quick access to diagonals
        if (location.getRow() == location.getColumn()) {
            if (_diagonal.get(location.getRow()) != null) {
                _rows.get(location.getRow()).remove(_diagonal.get(location.getRow()));
                _columns.get(location.getColumn()).remove(_diagonal.get(location.getColumn()));
                _diagonal.set(location.getRow(), null);
                return true;
            }
            return false;
        }

        // General case
        ISparseMatrixElement elt = _rows.get(location.getRow()).find(location.getColumn());
        if (elt == null)
            return false;
        _rows.get(location.getRow()).remove(elt);
        _columns.get(location.getColumn()).remove(elt);
        return true;
	}
	/**
	 * Finds the SparseMatrixElement on the diagonal
	 * 
	 * @param index
	 * @return The matrix element if it exists; otherwise null
	 */
	public ISparseMatrixElement findDiagonalElement(int index){
		assert(index >= 0);
		
        if (index > getSize())
            return null;
        return _diagonal.get(index);
	}
	
	/**
	 * Finds a pointer to the matrix element at the specified row and column. If
     * the element doesn't exist, null is returned.
     * 
	 * @param location The matrix location
	 * @return The matrix element if it exists; otherwise null
	 */
	public ISparseMatrixElement findElement(MatrixLocation location){
		if (location.getRow() > getSize() || location.getColumn() > getSize())
            return null;
        if (location.getRow() == 0 || location.getColumn() == 0)
            return _trashCan;

        // Find the element
        return _rows.get(location.getRow()).find(location.getColumn());
	}
	
	/**
	 * Gets the first non-default SparseMatrixElement in the specified row
	 * 
	 * @param row The row index
	 * @return The matrix element
	 */
	public ISparseMatrixElement getFirstInRow(int row){
		assert(row >= 0);
		return _rows.get(row).getFirstInRow();	
	}
	/**
	 * Gets the last non-default SparseMatrixElement in the specified row
	 * 
	 * @param row The row index
	 * @return The matrix element
	 */
	public ISparseMatrixElement getLastInRow(int row){
		assert(row >= 0);
		return _rows.get(row).getLastInRow();	
	}
	/**
	 * Gets the first non-default SparseMatrixElement in the specified column
	 * 
	 * @param column The column index
	 * @return The matrix element
	 */
	public ISparseMatrixElement getFirstInColumn(int column){
		assert(column >= 0);
		return _columns.get(column).getFirstInColumn();
	}
	/**
	 * Gets the last non-default SparseMatrixElement in the specified column
	 * 
	 * @param column The column index
	 * @return The matrix element
	 */
	public ISparseMatrixElement getLastInColumn(int column){
		assert(column >= 0);
		return _columns.get(column).getLastInColumn();
	}
	
	@Override
	public void swapRows(int row1, int row2) {
		// TODO Auto-generated method stub
		assert(row1 >= 0);
		assert(row2 >= 0);
		
        if (row1 == row2) {
            return;
        }
        // Simplify algorithm: first index is always the lowest one
        if (row2 < row1){
            int tmp = row1;
            row1 = row2;
            row2 = tmp;
        }
        if (row2 > getSize()) {
            expand(row2);
        }
        // Get the two elements
        ISparseMatrixElement row1Element = _rows.get(row1).getFirstInRow();
        ISparseMatrixElement row2Element = _rows.get(row2).getFirstInRow();

        // Swap the two rows
        SparseMatrixRow tmpRow = _rows.get(row1);
        _rows.set(row1, _rows.get(row2));
        _rows.set(row2, tmpRow);

        // Reset the diagonal elements
        _diagonal.set(row1, null);
        _diagonal.set(row2, null);

        // Swap the elements from left to right
        while (row1Element != null || row2Element != null){
            if (row2Element == null) {
                _columns.get(row1Element.getColumn()).swap(row1Element, null, row1, row2);
                if (row1Element.getColumn() == row2)
                    _diagonal.set(row1Element.getColumn(), row1Element);
                row1Element = row1Element.getRight();
                
            } else if (row1Element == null) {
                _columns.get(row2Element.getColumn()).swap(null, row2Element, row1, row2);
                if (row2Element.getColumn() == row1) {
                    _diagonal.set(row2Element.getColumn(), row2Element);
                }
                row2Element = row2Element.getRight();
            } else if (row1Element.getColumn() < row2Element.getColumn()) {
                _columns.get(row1Element.getColumn()).swap(row1Element, null, row1, row2);
                if (row1Element.getColumn() == row2) {
                    _diagonal.set(row1Element.getColumn(), row1Element);
                }
                row1Element = row1Element.getRight();
            } else if (row2Element.getColumn() < row1Element.getColumn()) {
                _columns.get(row2Element.getColumn()).swap(null, row2Element, row1, row2);
                if (row2Element.getColumn() == row1) {
                    _diagonal.set(row2Element.getColumn(), row2Element);
                }
                row2Element = row2Element.getRight();
            } else {
                _columns.get(row1Element.getColumn()).swap(row1Element, row2Element, row1, row2);

                // Update diagonals
                if (row1Element.getColumn() == row2) {
                    _diagonal.set(row1Element.getColumn(), row1Element);
                } else if (row2Element.getColumn() == row1) {
                    _diagonal.set(row2Element.getColumn(), row2Element);
            	}
                row1Element = row1Element.getRight();
                row2Element = row2Element.getRight();
            }
        }		
	}

	@Override
	public void swapColumns(int column1, int column2) {
		// TODO Auto-generated method stub
		assert(column1 >= 0);
		assert(column2 >= 0);
		
        if (column1 == column2) {
            return;
        }
        // Simplify algorithm: first index is always the lowest one
        if (column2 < column1){
            int tmp = column1;
            column1 = column2;
            column2 = tmp;
        }
        if (column2 > getSize()) {
            expand(column2);
        }
        // Get the two elements
        ISparseMatrixElement column1Element = _columns.get(column1).getFirstInColumn();
        ISparseMatrixElement column2Element = _columns.get(column2).getFirstInColumn();

        // Swap the two rows
        SparseMatrixColumn tmpColumn = _columns.get(column1);
        _columns.set(column1, _columns.get(column2));
        _columns.set(column2, tmpColumn);

        // Reset the diagonal elements
        _diagonal.set(column1, null);
        _diagonal.set(column2, null);

        // Swap the elements from left to right
        while (column1Element != null || column2Element != null){
            if (column2Element == null) {
                _rows.get(column1Element.getRow()).swap(column1Element, null, column1, column2);
                if (column1Element.getRow() == column2)
                    _diagonal.set(column1Element.getRow(), column1Element);
                column1Element = column1Element.getBelow();
                
            } else if (column1Element == null) {
                _rows.get(column2Element.getRow()).swap(null, column2Element, column1, column2);
                if (column2Element.getRow() == column1) {
                    _diagonal.set(column2Element.getRow(), column2Element);
                }
                column2Element = column2Element.getBelow();
            } else if (column1Element.getRow() < column2Element.getRow()) {
                _rows.get(column1Element.getRow()).swap(column1Element, null, column1, column2);
                if (column1Element.getRow() == column2) {
                    _diagonal.set(column1Element.getRow(), column1Element);
                }
                column1Element = column1Element.getBelow();
            } else if (column2Element.getRow() < column1Element.getRow()) {
                _rows.get(column2Element.getRow()).swap(null, column2Element, column1, column2);
                if (column2Element.getRow() == column1) {
                    _diagonal.set(column2Element.getRow(), column2Element);
                }
                column2Element = column2Element.getBelow();
            } else {
                _rows.get(column1Element.getRow()).swap(column1Element, column2Element, column1, column2);

                // Update diagonals
                if (column1Element.getRow() == column2) {
                    _diagonal.set(column1Element.getRow(), column1Element);
                } else if (column2Element.getRow() == column1) {
                    _diagonal.set(column2Element.getRow(), column2Element);
            	}
                column1Element = column1Element.getBelow();
                column2Element = column2Element.getBelow();
            }
        }
	}
	
	@Override
	public void reset() {
		_trashCan.setValue(0.0);
        for (int r = 1; r <= getSize(); r++) {
            ISparseMatrixElement elt = getFirstInRow(r);
            while (elt != null) {
                elt.setValue(0.0);
                elt = elt.getRight();
            }
        }
	}

	@Override
	public void clear() {
		_trashCan.setValue(0.0);
        for (int i = 1; i < _columns.size(); i++) {
            _columns.get(i).clear();
        }
        for (int i = 1; i < _rows.size(); i++) {
            _rows.get(i).clear();
        }
        for (int i = 0; i < _diagonal.size(); i++) {
            _diagonal.set(i, null);
        }
        _columns = new ArrayList<SparseMatrixColumn>(INITIAL_SIZE + 1);
        _rows = new ArrayList<SparseMatrixRow>(INITIAL_SIZE + 1);
        _diagonal = new ArrayList<ISparseMatrixElement>(INITIAL_SIZE + 1);

        _allocatedSize = INITIAL_SIZE;
        setSize(0);
        _elementCount = 1;
	}
	
	@Override
	public String toString(){
		return String.format("Sparse matrix (%Xx%X)", getSize());
	}
	
	/**
	 * Gets the number of elements in the matrix.
	 * 
	 * @return The element count
	 */
	public int getElementCount() {
		return _elementCount;	
	}

	@Override
	public int getSize() {
		return _size;
	}
	
	private void setSize(int size) {
		_size = size;
	}

	@Override
	public double get(int row, int column) {
		ISparseMatrixElement element = findElement(new MatrixLocation(row, column));
        return element.getValue();
	}

	@Override
	public void set(int row, int column, double value) {
		MatrixLocation location = new MatrixLocation(row, column);
		set(location, value);
	}

	@Override
	public double get(MatrixLocation location) {
		ISparseMatrixElement element = findElement(location);
        return element.getValue();
	}

	@Override
	public void set(MatrixLocation location, double value) {
		// We have to create an element if it doesn't exist yet
	   	ISparseMatrixElement element = getElement(location);
	   	element.setValue(value);
	}
	
	private void expand(int newSize) {
		// Only expanding here!
		if (newSize <= getSize())
			return;

		// Current size
		setSize(newSize);

		// No need to allocate new vectors
		if (newSize <= _allocatedSize)
			return;
		int oldAllocatedSize = _allocatedSize;

		// Allocate some extra space if necessary
		newSize = Math.max(newSize, (int) (_allocatedSize * EXPANSION_FACTOR));
		
		// Resize rows
		for (int i = oldAllocatedSize + 1; i <= newSize; i++) {
			_rows.add(new SparseMatrixRow());
		}
		// Resize columns
		for (int i = oldAllocatedSize + 1; i <= newSize; i++) {
			_columns.add(new SparseMatrixColumn());
		}
		// Other
		for (int i = oldAllocatedSize + 1; i <= newSize; i++) {
			_diagonal.add(null);
		}
		_allocatedSize = newSize;
	}

}
