package com.aljebra;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.algebra.diagnostics.AlgebraException;
import com.algebra.solve.lu.densesolver.DenseLUSolver;
import com.algebra.vector.densevector.DenseVector;

public class DenseSolverTests {
	
	@Test
    public void simpleMatrixTest() throws AlgebraException {
		DenseLUSolver solver = new DenseLUSolver();
		
		double[][] matrix = new double[][]{
			new double[] { 1.0, 1.0, 1.0, 1.0 },
	        new double[] { 0.0, 2.0, 0.0, 0.0 },
	        new double[] { 0.0, 0.0, 4.0, 0.0 },
	        new double[] { 0.0, 0.0, 0.0, 8.0 }
	    };
	    
	    for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                solver.set(r + 1, c + 1, matrix[r][c]);
            }
	    }

	    solver.set(1, 2);
		solver.set(2, 4);
		solver.set(3, 5);
		solver.set(4, 8);
		
		DenseVector solution = new DenseVector(4);
		
		assertEquals(4, solver.orderAndFactor());
		
		solver.solve(solution);
		assertEquals(-9.0 / 4.0, solution.get(1), 1e-12);
        assertEquals(2.0, solution.get(2), 1e-12);
        assertEquals(5.0 / 4.0, solution.get(3), 1e-12);
        assertEquals(1.0, solution.get(4), 1e-12);
    }
	
	@Test
	public void gearTest() throws AlgebraException{
		DenseLUSolver solver = new DenseLUSolver();
        for (int i = 1; i <= 4; i++) {
            solver.set(1, i, 1);
        }
        for (int i = 2; i <= 4; i++) {
            solver.set(i, 2, 1);
        }
        solver.set(2, 3, 1.5);
        solver.set(3, 3, 2.25);
        solver.set(4, 3, 3.375);
        solver.set(2, 4, 1.75);
        solver.set(3, 4, 3.0625);
        solver.set(4, 4, 5.359375);
        solver.set(2, -25000000);

        solver.factor();
        DenseVector sol = new DenseVector(4);
        solver.solve(sol);
        double[] reference = new double[] { 5.595238095238093e+07, -1.749999999999999e+08, 2.333333333333332e+08, -1.142857142857142e+08 };

        for (int i = 0; i < sol.length(); i++){
            double tol = Math.max(Math.abs(sol.get(i + 1)), Math.abs(reference[i])) * 1e-12;
            assertEquals(reference[i], sol.get(i + 1), tol);
        }
    }

}
