package com.algebra.solve;

import com.algebra.diagnostics.AlgebraException;
import com.algebra.vector.IVector;

public interface ISolver {

    int getDegeneracy();
    
    void setDegeneracy(int degeneracy);

    int size();

    boolean isFactored();

    void solve(IVector solution) throws AlgebraException;
    
    void solve(IVector solution, int size) throws AlgebraException;

    void solveTransposed(IVector solution) throws AlgebraException;

    boolean factor();

    void reset();

    void clear();

}
