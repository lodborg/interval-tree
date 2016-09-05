package com.lodborg.intervaltree;

import java.util.Date;

public class DateInterval extends Interval <Date> {

	public DateInterval(){}

	public DateInterval(Date start, Date end, Bounded type){
		super(start, end, type);
	}

	public DateInterval(Date value, Unbounded type){
		super(value, type);
	}

	@Override
	protected Interval<Date> create() {
		return new DateInterval();
	}

	@Override
	public Date getMidpoint() {
		long start = getStart() == null ? new Date(0).getTime() : getStart().getTime();
		long end = getEnd() == null ? new Date(Long.MAX_VALUE).getTime() : getEnd().getTime();
		return new Date(start + (end-start)/2);
	}
}
