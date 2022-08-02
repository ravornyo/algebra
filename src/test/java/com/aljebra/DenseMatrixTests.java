package com.aljebra;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.algebra.matrix.densematrix.DenseMatrix;

public class DenseMatrixTests {

	@Test
	public void expandTest() {
		DenseMatrix n = new DenseMatrix();
		n.set(10, 10, 3);

		assertEquals(10, n.getSize());
		assertEquals(3.0, n.get(10, 10), 1e-12);

		n.clear();

		assertEquals(0, n.getSize());
		assertEquals(0.0, n.get(10, 10), 1e-12);
	}

	@Test
	public void swappingRowsTest() {
		DenseMatrix n = new DenseMatrix();
		for (int r = 1; r < 10; r++)
			for (int c = 1; c < 10; c++)
				n.set(r, c, (r - 1) * 10 + c);

		n.swapRows(2, 5);

		for (int r = 1; r < 10; r++) {
			int row = r == 2 ? 5 : r == 5 ? 2 : r;
			for (int c = 1; c < 10; c++)
				assertEquals((row - 1) * 10 + c, n.get(r, c), 1e-12);
		}

		n.clear();
	}

	@Test
	public void swappingColumnsTest() {
		DenseMatrix n = new DenseMatrix();
		for (int r = 1; r < 10; r++)
			for (int c = 1; c < 10; c++)
				n.set(r, c, (r - 1) * 10 + c);

		n.swapColumns(2, 5);

		for (int r = 1; r < 10; r++) {
			for (int c = 1; c < 10; c++) {
				int col = c == 2 ? 5 : c == 5 ? 2 : c;
				assertEquals((r - 1) * 10 + col, n.get(r, c), 1e-12);
			}
		}

		n.clear();
	}

}
