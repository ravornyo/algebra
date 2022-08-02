package com.algebra.solve;

import com.algebra.matrix.IMatrix;
import com.algebra.matrix.MatrixLocation;
import com.algebra.vector.IVector;

public abstract class PivotingSolver<M extends IMatrix, V extends IVector> implements IPivotingSolver<M, V>{
	
	private int _pivotSearchReduction = 0;
    private int _degeneracy = 0;
    private boolean _needsReordering;
    private boolean _factored;
    private Translation _row = new Translation();
    private Translation _column = new Translation();
    private M _matrix;
    private V _vector;
    
    public PivotingSolver(M matrix, V vector) {
    	_matrix = matrix;
        _vector = vector;
        setNeedsReordering(true);
        setFactored(false);
    }
    
	@Override
	public int size() {
		return Math.max(getMatrix().getSize(), getVector().length());
	}
    
    public double get(int row) {
    	row = _row.get(row);
    	return _vector.get(row);
    }
    
    public void set(int row, double value) {
    	row = _row.get(row);
    	_vector.set(row, value);
    }
    
    public double get(int row, int column) {
    	return getMatrix().get(row, column);
    }
    
    public void set(int row, int column, double value) {
    	getMatrix().set(row, column, value);
    }
    
    public double get(MatrixLocation location) {
    	return getMatrix().get(location);
    }
    
    public void set(MatrixLocation location, double value) {
    	getMatrix().set(location, value);
    }
    
    public void swapRows(int row1, int row2){
        getMatrix().swapRows(row1, row2);
        getVector().swapElements(row1, row2);
        getRow().swap(row1, row2);
    }
    
    public void swapColumns(int column1, int column2) {
    	getMatrix().swapColumns(column1, column2);
        getColumn().swap(column1, column2);
    }
    
    public void reset(){
    	getMatrix().reset();
    	getVector().reset();
        setFactored(false);
    }

    public void clear(){
    	getMatrix().clear();
    	getVector().clear();
    	getRow().clear();
    	getColumn().clear();
    	setFactored(false);
        setNeedsReordering(true);
        setDegeneracy(0);
    }
    
    public MatrixLocation internalToExternal(MatrixLocation indices) {
    	return new MatrixLocation(getRow().reverse(indices.row), getColumn().reverse(indices.column));
    }

    public MatrixLocation externalToInternal(MatrixLocation indices) {
    	return new MatrixLocation(getRow().get(indices.row), getColumn().get(indices.column));
    }
    
	public int getPivotSearchReduction() {
		return _pivotSearchReduction;
	}
	public void setPivotSearchReduction(int _pivotSearchReduction) {
		this._pivotSearchReduction = _pivotSearchReduction;
	}
	public int getDegeneracy() {
		return _degeneracy;
	}
	public void setDegeneracy(int _degeneracy) {
		this._degeneracy = _degeneracy;
	}
	public void setNeedsReordering(boolean _needsReordering) {
		this._needsReordering = _needsReordering;
	}
	@Override
	public boolean needsReordering() {
		return _needsReordering;
	}

	public Translation getRow() {
		return _row;
	}
	public Translation getColumn() {
		return _column;
	}
	public M getMatrix() {
		return _matrix;
	}
	public V getVector() {
		return _vector;
	}

	public boolean isFactored() {
		return _factored;
	}

	public void setFactored(boolean _factored) {
		this._factored = _factored;
	}

}
