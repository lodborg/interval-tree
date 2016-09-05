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
		long from = getStart() == null ? Integer.MIN_VALUE : getStart();
		long to = getEnd() ==  null ? Integer.MAX_VALUE : getEnd();
		return (int)((from + to)/2);
	}

	@Override
	public boolean isEmpty() {
		if (getStart() == null || getEnd() == null)
			return false;
		if (getStart()+1 == getEnd() && !isEndInclusive() && !isStartInclusive())
			return true;
		return super.isEmpty();
	}
}
