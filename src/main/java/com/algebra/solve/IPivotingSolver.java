package com.algebra.solve;

import com.algebra.diagnostics.AlgebraException;
import com.algebra.matrix.IMatrix;
import com.algebra.matrix.MatrixLocation;
import com.algebra.vector.IVector;

public interface IPivotingSolver<M extends IMatrix, V extends IVector> extends ISolver{

    int getPivotSearchReduction();
    void setPivotSearchReduction(int pivotSearchReduction);

    boolean needsReordering();
    void setNeedsReordering(boolean needsReordering);

    void precondition(PreconditioningMethod<M, V> method);

    int orderAndFactor() throws AlgebraException;

    MatrixLocation internalToExternal(MatrixLocation indices);

    MatrixLocation externalToInternal(MatrixLocation indices);
}
