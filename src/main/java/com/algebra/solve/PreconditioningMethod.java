package com.algebra.solve;

@FunctionalInterface
public interface PreconditioningMethod<M, V> {
    void apply(M m, V v);
}
