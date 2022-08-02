package com.algebra.matrix.densematrix;

import com.algebra.matrix.IMatrix;
import com.algebra.matrix.MatrixLocation;

/**
 * A square matrix using a dense representation
 * 
 * @author avornyo
 *
 * @param  The base value type
 */
public class DenseMatrix implements IMatrix {
	
	private double[] _array;
    private double _trashCan;
    private int _size;
    private int _allocatedSize;

    private final int _initialSize = 4;
    private final float _expansionFactor = 1.25f;
	
	public DenseMatrix() {
		_size = 0;
        _allocatedSize = _initialSize;
        _array = new double[_allocatedSize * _allocatedSize];
        _trashCan = 0;
	}
	
	public DenseMatrix(int size) {
		_size = size;
        _allocatedSize = Math.max(_initialSize, size);
        _array = new double[_allocatedSize * _allocatedSize];
        _trashCan = 0;
	}

	@Override
	public int getSize() {
		return _size;
	}

	@Override
	public double get(int row, int column) {
        if (row == 0 || column == 0) {
            return _trashCan;
        }
        if (row > _size || column > _size) {
            return 0.0;
        }
        return _array[(row - 1) * _allocatedSize + column - 1];
	}

	@Override
	public void set(int row, int column, double value) {
        if (row == 0 || column == 0) {
            _trashCan = value;
        } else {
            if (value != 0.0 && (row > _size || column > _size)) {
                expand(Math.max(row, column));
            }
            _array[(row - 1) * _allocatedSize + column - 1] = value;
        }
	}

	@Override
	public double get(MatrixLocation location) {
		return get(location.row, location.column);
	}

	@Override
	public void set(MatrixLocation location, double value) {
		set(location.row, location.column, value);
	}

	@Override
	public void swapRows(int row1, int row2) {
		if (row1 == row2 || (row1 > _size && row2 > _size))
            return;

        // Expand the matrix if necessary
        int needed = Math.max(row1, row2);
        if (needed > _size) {
            expand(needed);
        }

        int offset1 = (row1 - 1) * _allocatedSize;
        int offset2 = (row2 - 1) * _allocatedSize;
        for (int i = 0; i < _size; i++) {
        	double tmp = _array[offset1 + i];
            _array[offset1 + i] = _array[offset2 + i];
            _array[offset2 + i] = tmp;
        }
	}

	@Override
	public void swapColumns(int column1, int column2) {
        if (column1 == column2 || (column1 > _size && column2 > _size))
            return;

        // Expand the matrix if necessary
        int needed = Math.max(column1, column2);
        if (needed > _size)
            expand(needed);

        column1--;
        column2--;
        for (int i = 0; i < _allocatedSize * _allocatedSize; i += _allocatedSize){
        	double tmp = _array[column1 + i];
            _array[column1 + i] = _array[column2 + i];
            _array[column2 + i] = tmp;
        }
	}

	@Override
	public void reset() {
		for (int i = 0; i < _array.length; i++)
            _array[i] = 0;
	}

	@Override
	public void clear() {
		_trashCan = 0.0;
        _array = new double[_initialSize * _initialSize];
        _allocatedSize = _initialSize;
        _size = 0;
	}

	private void expand(int newSize){
        int oldSize = _size;
        _size = newSize;
        if (newSize <= _allocatedSize)
            return;
        newSize = Math.max(newSize, (int)(_allocatedSize * _expansionFactor));

        // Create a new array and copy its contents
        double[] nArray = new double[newSize * newSize];
        for (int r = 0; r < oldSize; r++) {
            for (int c = 0; c < oldSize; c++) {
                nArray[r * newSize + c] = _array[r * _allocatedSize + c];
            }
        }
        _array = nArray;
        _allocatedSize = newSize;
    }

}
