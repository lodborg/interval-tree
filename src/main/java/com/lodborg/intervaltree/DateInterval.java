package com.lodborg.intervaltree;

import java.util.Date;

/**
 * A class representing an interval with Dates as start and end points.
 */
public class DateInterval extends Interval<Date> {

	/**
	 * Instantiates an interval extending from positive infinity to negative
	 * infinity and thus containing all Dates.
	 */
	public DateInterval(){}

	/**
	 * Instantiates a new bounded interval.
	 *
	 * @param start The start date of the interval
	 * @param end The end date of the interval.
	 * @param type Description of whether the interval is open/closed at one or both
	 *             of its ends. See {@link Bounded the documentation of the Bounded enum}
	 *             for more information on the different possibilities.
	 */
	public DateInterval(Date start, Date end, Bounded type){
		super(start, end, type);
	}

	/**
	 * Instantiates a new unbounded interval - an interval that extends to positive or
	 * negative infinity. The interval will be bounded by either the start point
	 * or the end point and unbounded in the other point.
	 *
	 * @param value The bounding date for either the start or the end point.
	 * @param type Describes whether the interval extends to positive or negative infinity,
	 *             as well as if it is open or closed at the bounding point. See {@link Unbounded
	 *             the Unbounded enum} for description of the different possibilities.
	 */
	public DateInterval(Date value, Unbounded type){
		super(value, type);
	}

	@Override
	protected Interval<Date> create() {
		return new DateInterval();
	}

	/**
	 * Determines the center of the interval.
	 * <p>
	 *     Similarly to the class {@link IntegerInterval}, it assumes that unbounded intervals
	 *     are bounded by Dates with timestamps Long.MIN_VALUE and/or Long.MAX_VALUE. The center
	 *     of an unbounded interval is computed accordingly.
	 * </p>
	 *
	 * @return The center of the interval.
	 */
	@Override
	public Date getMidpoint() {
		if (isEmpty())
			return null;
		long start = getStart() == null ? new Date(Long.MIN_VALUE).getTime() : getStart().getTime();
		long end = getEnd() == null ? new Date(Long.MAX_VALUE).getTime() : getEnd().getTime();

		// If the calculation would return the start point and the start point
		// is actually not in the interval, return the end point.
		if (start+1 == end && !isStartInclusive())
			return getEnd();

		// Prevent an overflow
		if (start <= 0 && end >= 0)
			return new Date((end + start) / 2);

		return new Date(start + (end-start)/2);
	}
}
