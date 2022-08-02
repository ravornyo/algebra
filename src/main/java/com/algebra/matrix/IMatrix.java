package com.algebra.matrix;

/**
 * Describes a matrix.
 * 
 * @author avornyo
 *
 */
public interface IMatrix {
	/**
	 * Gets the size of the matrix.
	 * 
	 * @return The matrix size
	 */
	int getSize();
	/**
	 * Gets the value at the specified row and column
	 * 
	 * @param row The row index
	 * @param column The column index
	 * @return The value
	 */
    double get(int row, int column);
    /**
     * Sets the value at the specified row and column
     * 
     * @param row The row index
     * @param column The column index
     * @param value The value
     */
    void set(int row, int column, double value);
    /**
     * Gets the value at the specified location
     * 
     * @param location
     * @return The value
     */
    double get(MatrixLocation location);
    /**
     * Sets the value at the specified location
     * 
     * @param location
     * @param value The value
     */
    void set(MatrixLocation location, double value);
    /**
     * Swaps two rows in the matrix
     * 
     * @param row1 The first row index
     * @param row2 The second row index
     */
    void swapRows(int row1, int row2);
    /**
     * Swaps two columns in the matrix
     * 
     * @param column1 The first column index
     * @param column2 The second column index
     */
    void swapColumns(int column1, int column2);
    /**
     * Resets all elements in the matrix to their default value
     */
    void reset();
    /**
     * Clears the matrix of any elements. The size of the matrix becomes 0.
     */
    void clear();
}
