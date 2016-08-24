package com.lodborg.intervaltree;

public class IntegerInterval extends Interval<Integer> {

	public IntegerInterval(){}

	public IntegerInterval(Integer start, Integer end, Bounded type){
		super(start, end, type);
	}

	public IntegerInterval(Integer value, Unbounded type){
		super(value, type);
	}

	@Override
	protected IntegerInterval create(Integer start, boolean isStartInclusive, Integer end, boolean isEndInclusive){
		IntegerInterval interval = new IntegerInterval();
		interval.start = start;
		interval.isStartInclusive = isStartInclusive;
		interval.end = end;
		interval.isEndInclusive = isEndInclusive;
		return interval;
	}

	@Override
	boolean isEmpty() {
		if (start == null || end == null)
			return false;
		if (start > end)
			return true;
		if (start.equals(end) && (!isEndInclusive || !isStartInclusive))
			return true;
		if (start+1 == end && !isEndInclusive && !isStartInclusive)
			return true;
		return false;
	}
}
