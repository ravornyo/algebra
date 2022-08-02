package com.aljebra;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.algebra.solve.lu.densesolver.DenseLUSolver;

public class DenseFactorTests {

	@Test
	public void factoringTest() {
		double[][] matrixElements = new double[][] { 
			new double[] { 1.0, 1.0, 1.0 }, 
			new double[] { 2.0, 3.0, 5.0 },
			new double[] { 4.0, 6.0, 8.0 } 
		};
		double[][] expected = new double[][] { 
			new double[] { 1.0, 1.0, 1.0 }, 
			new double[] { 2.0, 1.0, 3.0 },
			new double[] { 4.0, 2.0, -0.5 } 
		};

		DenseLUSolver solver = new DenseLUSolver();
		for (int r = 0; r < matrixElements.length; r++) {
			for (int c = 0; c < matrixElements[r].length; c++) {
				solver.set(r + 1, c + 1, matrixElements[r][c]);
			}
		}

		// Factor
		solver.factor();

		// Compare
		for (int r = 0; r < matrixElements.length; r++) {
			for (int c = 0; c < matrixElements[r].length; c++) {
				assertEquals(expected[r][c], solver.get(r + 1, c + 1), 1e-12);
			}
		}
	}
/*
	@Test
	public void orderAndFactoringTest() {
		DenseLUSolver solver = new DenseLUSolver();
		solver.set(1, 1, 1);
		solver.set(1, 4, -1);
		solver.set(2, 1, -1);
		solver.set(2, 3, 1);
		solver.set(3, 2, 1);
		solver.set(4, 5, 1);
		solver.set(5, 4, 1);

		// Order and factor
		assertEquals(solver.size(), solver.orderAndFactor());

		// Compare
		assertEquals(1.0, solver.get(1, 1));
		assertEquals(-1.0, solver.get(1, 4));
		assertEquals(-1.0, solver.get(2, 1));
		assertEquals(1.0, solver.get(2, 3));
		assertEquals(-1.0, solver.get(2, 4));
		assertEquals(1.0, solver.get(3, 2));
		assertEquals(1.0, solver.get(4, 5));
		assertEquals(1.0, solver.get(5, 4));
	}
*/
}
