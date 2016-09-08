package com.lodborg.intervaltree;

public class DoubleInterval extends Interval<Double> {

	public static final int OFFSET = 1_000;

	public DoubleInterval(){ }

	public DoubleInterval(Double start, Double end, Bounded type){
		super(0.0 + start, 0.0 + end, type);
	}

	public DoubleInterval(Double value, Unbounded type){
		super(0.0 + value, type);
	}

	@Override
	protected Interval<Double> create() {
		return new DoubleInterval();
	}

	@Override
	public boolean isEmpty() {
		if (getStart() != null && getStart().isNaN())
			return true;
		if (getEnd() != null && getEnd().isNaN())
			return true;
		if (getStart() != null && getEnd() != null) {
			if (getStart() == Double.POSITIVE_INFINITY && getEnd() == Double.POSITIVE_INFINITY)
				return true;
			if (getStart() == Double.NEGATIVE_INFINITY && getEnd() == Double.NEGATIVE_INFINITY)
				return true;
		}
		return super.isEmpty();
	}

	@Override
	public Double getMidpoint() {
		if (isEmpty())
			return null;

		// Handle null values
		if (getStart() == null && getEnd() == null)
			return 0.0;
		if (getStart() == null)
			return getEnd() - OFFSET;
		if (getEnd() == null)
			return getStart() + OFFSET;

		// Now we are sure there are no more null values involved
		if (getStart() == Double.NEGATIVE_INFINITY && getEnd() == Double.POSITIVE_INFINITY)
			return 0.0;
		if (getStart() == Double.NEGATIVE_INFINITY)
			return getEnd() - OFFSET;
		if (getEnd() == Double.POSITIVE_INFINITY)
			return getStart() + OFFSET;
		return getStart() + (getEnd() - getStart())/2;
	}
}
