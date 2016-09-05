package com.lodborg.intervaltree;

public class DoubleInterval extends Interval<Double> {

	public DoubleInterval(){}

	public DoubleInterval(Double start, Double end, Bounded type){
		super(start, end, type);
	}

	public DoubleInterval(Double value, Unbounded type){
		super(value, type);
	}

	@Override
	protected DoubleInterval create(){
		return new DoubleInterval();
	}

	@Override
	public Double getMidpoint() {
		if (isEmpty())
			return null;
		double from = getStart() == null ? -Double.MAX_VALUE : getStart();
		double to = getEnd() ==  null ? Double.MAX_VALUE : getEnd();
		return (from + to)/2;
	}
}
