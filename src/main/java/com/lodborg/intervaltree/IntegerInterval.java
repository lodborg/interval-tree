package com.lodborg.intervaltree;

/**
 * A class representing an interval with Integers as start and end points.
 */
public class IntegerInterval extends Interval<Integer> {

	/**
	 * Instantiates an interval extending from positive infinity to negative
	 * infinity and thus containing all Integers.
	 */
	public IntegerInterval(){}

	/**
	 * Instantiates a new bounded interval.
	 *
	 * @param start The start point of the interval
	 * @param end The end point of the interval.
	 * @param type Description of whether the interval is open/closed at one or both
	 *             of its ends. See {@link Bounded the documentation of the Bounded enum}
	 *             for more information on the different possibilities.
	 */
	public IntegerInterval(Integer start, Integer end, Bounded type){
		super(start, end, type);
	}

	/**
	 * Instantiates a new unbounded interval - an interval that extends to positive or
	 * negative infinity. The interval will be bounded by either the start point
	 * or the end point and unbounded in the other point.
	 *
	 * @param value The bounding value for either the start or the end point.
	 * @param type Describes whether the interval extends to positive or negative infinity,
	 *             as well as if it is open or closed at the bounding point. See {@link Unbounded
	 *             the Unbounded enum} for description of the different possibilities.
	 */
	public IntegerInterval(Integer value, Unbounded type){
		super(value, type);
	}

	@Override
	protected IntegerInterval create(){
		return new IntegerInterval();
	}

	/**
	 * Determines the center of the interval.
	 * <p>
	 * For the purposes of this method, if the interval is defined unbounded, it
	 * is assumed to be bounded by Integer.MIN_VALUE, if it extends to negative
	 * infinity, and/or Integer.MAX_VALUE, if it extends to positive infinity.
	 * </p>
	 * @return The midpoint of the interval.
	 */
	@Override
	public Integer getMidpoint() {
		if (isEmpty())
			return null;
		long from = getStart() == null ? Integer.MIN_VALUE : getStart();
		long to = getEnd() ==  null ? Integer.MAX_VALUE : getEnd();
		return (int)((from + to)/2);
	}

	/**
	 * Determines if the interval is empty, meaning it contains no Integers. In
	 * particular, this method will return {@code true} for the open interval
	 * (4, 5), since there is no Integer within this interval, even though the
	 * start and end points are different.
	 *
	 * @return {@code true}, if the interval is empty, or {@code false} otherwise.
	 */
	@Override
	public boolean isEmpty() {
		if (getStart() == null || getEnd() == null)
			return false;
		if (getStart()+1 == getEnd() && !isEndInclusive() && !isStartInclusive())
			return true;
		return super.isEmpty();
	}
}
