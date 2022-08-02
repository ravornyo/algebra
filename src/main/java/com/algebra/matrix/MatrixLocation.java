package com.algebra.matrix;

/**
 * A simple bean for describing a matrix row/column location
 * 
 * @author avornyo
 *
 */
public class MatrixLocation {
	/*
	 * The row index
	 */
    public final int row;
    /*
	 * The column index
	 */
    public final int column;
	
    /**
     * Initializes a new instance of the MatrixLocation bean.
     * 
     * @param row The row
     * @param column The column
     */
	public MatrixLocation(int row, int column) {
		assert(row >= 0);
		assert(column >= 0);
		
		this.row = row;
		this.column = column;
	}
	
	public MatrixLocation() {
		this.row = 0;
		this.column = 0;
	}

	@Override
	public int hashCode() {
		return (Integer.hashCode(row) * 13) ^ Integer.hashCode(column);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MatrixLocation) {
			MatrixLocation ml = (MatrixLocation)obj;
            if (this.row != ml.row)
                return false;
            if (this.column != ml.column)
                return false;
            return true;
        }
        return false;
	}
	
	@Override
	public String toString() {
		return String.format("(%x, %x)", row, column);
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
}
