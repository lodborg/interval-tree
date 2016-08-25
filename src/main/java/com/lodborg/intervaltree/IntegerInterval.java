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
		int from = start == null ? Integer.MIN_VALUE : start;
		int to = end ==  null ? Integer.MAX_VALUE : end;
		return (int)(from + (to-(long)from)/2);
	}

	@Override
	public boolean isEmpty() {
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
