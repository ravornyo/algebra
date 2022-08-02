package com.aljebra;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.algebra.matrix.MatrixLocation;
import com.algebra.matrix.sparsematrix.ISparseMatrixElement;
import com.algebra.matrix.sparsematrix.SparseMatrix;

public class SparseMatrixTests {

	@Test
	public void matrixLinksTest() {
		int size = 20;
		int rStep = 7;
		int cStep = 11;

		SparseMatrix matrix = new SparseMatrix();
		for (int c = 0; c < rStep * size; c += rStep) {
			for (int r = 0; r < cStep * size; r += cStep) {
				int row = r % size;
				int col = c % size;
				int expected = row * size + col + 1;
				matrix.getElement(new MatrixLocation(row + 1, col + 1)).setValue(expected);
			}
		}

		// Check links from left to right
		ISparseMatrixElement element;
		for (int r = 0; r < size; r++) {
			element = matrix.getFirstInRow(r + 1);
			for (int c = 0; c < size; c++) {
				int expected = r * size + c + 1;
				assertEquals(expected, element.getValue(), 1e-12);
				element = element.getRight();
			}
		}

		// Check links from right to left
		for (int r = 0; r < size; r++) {
			element = matrix.getLastInRow(r + 1);
			for (int c = size - 1; c >= 0; c--) {
				int expected = r * size + c + 1;
				assertEquals(expected, element.getValue(), 1e-12);
				element = element.getLeft();
			}
		}

		// Check links from top to bottom
		for (int c = 0; c < size; c++) {
			element = matrix.getFirstInColumn(c + 1);
			for (int r = 0; r < size; r++) {
				int expected = r * size + c + 1;
				assertEquals(expected, element.getValue(), 1e-12);
				element = element.getBelow();
			}
		}

		// Check links from bottom to top
		for (int c = 0; c < size; c++) {
			element = matrix.getLastInColumn(c + 1);
			for (int r = size - 1; r >= 0; r--) {
				int expected = r * size + c + 1;
				assertEquals(expected, element.getValue(), 1e-12);
				element = element.getAbove();
			}
		}

		matrix.clear();
	}

	@Test
	public void swappingRowsTest() {
		// Build the matrix
		SparseMatrix matrix = new SparseMatrix();

		// We want to test all possible combinations for the row! We need 5 elements to
		// be able to test it
		for (int i = 0; i < 32; i++) {
			// Our counter "i" will represent in binary which elements need to be filled.
			int fill = i;
			for (int k = 0; k < 5; k++) {
				// Get whether or not the element needs to be filled
				if ((fill & 0x01) != 0) {
					int expected = k * 32 + i + 1;
					matrix.getElement(new MatrixLocation(k + 1, i + 1)).setValue(expected);
				}
				fill = (fill >> 1) & 0b011111;
			}
		}

		// Swap the two rows of interest
		matrix.swapRows(2, 4);

		// Find the elements back
		for (int i = 0; i < 32; i++) {
			int fill = i;
			for (int k = 0; k < 5; k++) {
				// Get the current row to test (remember we swapped them!)
				int crow = k + 1;
				if (crow == 2)
					crow = 4;
				else if (crow == 4)
					crow = 2;

				if ((fill & 0x01) != 0) {
					int expected = k * 32 + i + 1;
					assertEquals(expected, matrix.get(crow, i + 1), 1e-12);
				} else
					assertEquals(null, matrix.findElement(new MatrixLocation(crow, i + 1)));
				fill = (fill >> 1) & 0b011111;
			}
		}

		matrix.clear();
	}

	@Test
	public void swappingColumnsTest() {
		// Build the matrix
		SparseMatrix matrix = new SparseMatrix();

		// We want to test all possible combinations for the row! We need 5 elements to
		// be able to test it
		for (int i = 0; i < 32; i++) {
			// Our counter "i" will represent in binary which elements need to be filled.
			int fill = i;
			for (int k = 0; k < 5; k++) {
				// Get whether or not the element needs to be filled
				if ((fill & 0x01) != 0) {
					int expected = k * 32 + i + 1;
					matrix.getElement(new MatrixLocation(i + 1, k + 1)).setValue(expected);
				}
				fill = (fill >> 1) & 0b011111;
			}
		}

		// Swap the two rows of interest
		matrix.swapColumns(2, 4);

		// Find the elements back
		for (int i = 0; i < 32; i++) {
			int fill = i;
			for (int k = 0; k < 5; k++) {
				// Get the current row to test (remember we swapped them!)
				int ccolumn = k + 1;
				if (ccolumn == 2)
					ccolumn = 4;
				else if (ccolumn == 4)
					ccolumn = 2;

				if ((fill & 0x01) != 0) {
					int expected = k * 32 + i + 1;
					assertEquals(expected, matrix.get(i + 1, ccolumn), 1e-12);
				} else
					assertEquals(null, matrix.findElement(new MatrixLocation(i + 1, ccolumn)));
				fill = (fill >> 1) & 0b011111;
			}
		}

		matrix.clear();
	}

}
