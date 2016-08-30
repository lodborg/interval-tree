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
	protected IntegerInterval create(){
		return new IntegerInterval();
	}

	@Override
	public Integer getMidpoint() {
		if (isEmpty())
			return null;
		long from = start == null ? Integer.MIN_VALUE : start;
		long to = end ==  null ? Integer.MAX_VALUE : end;
		return (int)((from + to)/2);
	}

	@Override
	public boolean isEmpty() {
		if (start == null || end == null)
			return false;
		if (start+1 == end && !isEndInclusive && !isStartInclusive)
			return true;
		return super.isEmpty();
	}
}
