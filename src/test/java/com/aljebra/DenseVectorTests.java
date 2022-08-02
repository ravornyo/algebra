package com.aljebra;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.algebra.vector.densevector.DenseVector;

public class DenseVectorTests {

	@Test
	public void swappingVectorElementsTest() {
		DenseVector vector = new DenseVector(5);
		for (int k = 1; k <= 5; k++) {
			vector.set(k, k);
		}

		vector.swapElements(2, 4);

		for (int k = 1; k <= 5; k++) {
			assertEquals(k == 2 ? 4 : k == 4 ? 2 : k, vector.get(k), 1e-12);
		}
	}

}
