package com.algebra.solve;
/**
 * Describes a pivot for reordering solvers.
 * 
 * @author avornyo
 *
 * @param <P> The pivot output
 */
public class Pivot<P> {

	public final P element;
	public final PivotInfo info;

	public Pivot(PivotInfo info) {
		this(null, info);
	}
	
	public Pivot(P element, PivotInfo info) {
		this.element = element;
		this.info = info;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pivot) {
			@SuppressWarnings("rawtypes")
			Pivot pivot = (Pivot) obj;
			if (!element.equals(pivot.element))
				return false;
			if (!info.equals(pivot.info))
				return false;
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (element.hashCode()) ^ info.hashCode();
	}
	
	public static <P> Pivot<P> empty(){
		return new Pivot<P>(PivotInfo.None);
	}

}
