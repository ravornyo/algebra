package com.algebra.diagnostics;

@SuppressWarnings("serial")
public class SingularException extends AlgebraException {

	private int _index;

	public SingularException(int index) {
		_index = index;
	}

	public int getIndex() {
		return _index;
	}

}
