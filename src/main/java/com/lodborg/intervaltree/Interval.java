package com.lodborg.intervaltree;

import java.util.Comparator;

/**
 *
 * This abstract class could have been made an interface instead of a class to address the fact that there is no
 * multiple inheritance in Java. Since we wanted to include implementations for some of the main methods (like
 * contains() and intersects()) and also didn't want to use default interface methods (so that we can also support
 * Java 7), abstract class was our only option left.
 */
public abstract class Interval<T extends Comparable<? super T>> {
	protected T start, end;
	protected boolean isStartInclusive, isEndInclusive;

	public enum Bounded {
		OPEN, CLOSED, CLOSED_RIGHT, CLOSED_LEFT
	}

	public enum Unbounded {
		LEFT_OPEN, LEFT_CLOSED, RIGHT_OPEN, RIGHT_CLOSED
	}

	public Interval(){
		isStartInclusive = true;
		isEndInclusive = true;
	}

	public Interval(T start, T end, Bounded type){
		this.start = start;
		this.end = end;
		switch (type){
			case OPEN:
				break;
			case CLOSED:
				isStartInclusive = true;
				isEndInclusive = true;
				break;
			case CLOSED_RIGHT:
				isEndInclusive = true;
				break;
			case CLOSED_LEFT:
				isStartInclusive = true;
				break;
		}
	}

	public Interval(T value, Unbounded type){
		switch (type){
			case LEFT_OPEN:
				start = value;
				isStartInclusive = false;
				isEndInclusive = true;
				break;
			case LEFT_CLOSED:
				start = value;
				isStartInclusive = true;
				isEndInclusive = true;
				break;
			case RIGHT_OPEN:
				end = value;
				isStartInclusive = true;
				isEndInclusive = false;
				break;
			case RIGHT_CLOSED:
				end = value;
				isStartInclusive = true;
				isEndInclusive = true;
				break;
		}
	}

	public abstract boolean isEmpty();
	protected abstract Interval<T> create();
	public abstract T getMidpoint();

	protected Interval<T> create(T start, boolean isStartInclusive, T end, boolean isEndInclusive){
		Interval<T> interval = create();
		interval.start = start;
		interval.isStartInclusive = isStartInclusive;
		interval.end = end;
		interval.isEndInclusive = isEndInclusive;
		return interval;
	}

	public T getStart(){
		return start;
	}
	public T getEnd(){
		return end;
	}
	public boolean isStartInclusive(){
		return isStartInclusive;
	}
	public boolean isEndInclusive(){
		return isEndInclusive;
	}

	public boolean isPoint(){
		if (start == null || end == null) {
			return false;
		}
		return start.compareTo(end) == 0;
	}

	public boolean contains(T query){
		if (isEmpty()) {
			return false;
		}

		int startCompare;
		if (start == null){
			startCompare = query == null ? 0 : 1;
		} else {
			startCompare = query == null ? -1 : query.compareTo(start);
		}

		int endCompare;
		if (end == null){
			endCompare = query == null ? 0 : -1;
		} else {
			endCompare = query == null ? 1 : query.compareTo(end);
		}

		if (startCompare > 0 && endCompare < 0) {
			return true;
		}
		return (startCompare == 0 && isStartInclusive) || (endCompare == 0 && isEndInclusive);
	}

	public Interval<T> getIntersection(Interval<T> other){
		if (other == null)
			return null;
		if ((other.start == null && start != null) || (start != null && start.compareTo(other.start)>0))
			return other.getIntersection(this);
		if (end != null && other.start != null && (end.compareTo(other.start) < 0 || (end.compareTo(other.start) == 0 && (!isEndInclusive || !other.isStartInclusive))))
			return null;

		T newStart, newEnd;
		boolean isNewStartInclusive, isNewEndInclusive;

		if (other.start == null){
			newStart = null;
			isNewStartInclusive = true;
		} else {
			newStart = other.start;
			if (start != null && other.start.compareTo(start) == 0)
				isNewStartInclusive = other.isStartInclusive && isStartInclusive;
			else
				isNewStartInclusive = other.isStartInclusive;
		}

		if (end == null){
			newEnd = other.end;
			isNewEndInclusive = other.isEndInclusive;
		} else if (other.end == null){
			newEnd = end;
			isNewEndInclusive = isEndInclusive;
		} else {
			int compare = end.compareTo(other.end);
			if (compare == 0){
				newEnd = end;
				isNewEndInclusive = isEndInclusive && other.isEndInclusive;
			} else if (compare < 0){
				newEnd = end;
				isNewEndInclusive = isEndInclusive;
			} else {
				newEnd = other.end;
				isNewEndInclusive = other.isEndInclusive;
			}
		}
		return create(newStart, isNewStartInclusive, newEnd, isNewEndInclusive);
	}

	public boolean contains(Interval<T> another){
		if (another == null || isEmpty() || another.isEmpty()){
			return false;
		}
		Interval<T> intersection = getIntersection(another);
		return intersection != null && intersection.equals(another);
	}

	public boolean intersects(Interval<T> query){
		if (query == null)
			return false;
		Interval<T> intersection = getIntersection(query);
		return intersection != null && !intersection.isEmpty();
	}

	public boolean isRightOf(T point, boolean inclusive){
		if (point == null || start == null)
			return false;
		int compare = point.compareTo(start);
		if (compare != 0)
			return compare < 0;
		return !isStartInclusive() || !inclusive;
	}

	public boolean isRightOf(T point){
		return isRightOf(point, true);
	}

	public boolean isRightOf(Interval<T> other){
		return isRightOf(other.end, other.isEndInclusive());
	}

	public boolean isLeftOf(T point, boolean inclusive){
		if (point == null || end == null)
			return false;
		int compare = point.compareTo(end);
		if (compare != 0)
			return compare > 0;
		return !isEndInclusive() || !inclusive;
	}

	public boolean isLeftOf(T point){
		return isLeftOf(point, true);
	}

	public boolean isLeftOf(Interval<T> other){
		return isLeftOf(other.start, other.isStartInclusive());
	}

	private int compareStarts(Interval<T> other){
		if (start == null && other.start == null)
			return 0;
		if (start == null)
			return -1;
		if (other.start == null)
			return 1;
		int compare = start.compareTo(other.start);
		if (compare != 0)
			return compare;
		if (isStartInclusive ^ other.isStartInclusive)
			return isStartInclusive ? -1 : 1;
		return 0;
	}

	private int compareEnds(Interval<T> other){
		if (end == null && other.end == null)
			return 0;
		if (end == null)
			return 1;
		if (other.end == null)
			return -1;
		int compare = end.compareTo(other.end);
		if (compare != 0)
			return compare;
		if (isEndInclusive ^ other.isEndInclusive)
			return isEndInclusive ? 1 : -1;
		return 0;
	}

	/**
	 * A comparator that can be used as a parameter for sorting functions. The start comparator sorts the intervals
	 * in <em>ascending</em> order by placing the intervals with a smaller start point before intervals with greater
	 * start points. This corresponds to a line sweep from left to right.
	 *
	 * Intervals with start point null (negative infinity) are considered smaller than all other intervals.
	 * If two intervals have the same start point, the closed start point is considered smaller than the open one.
	 * For example, [0, 2) is considered smaller than (0, 2).
	 *
	 * To ensure that this comparator can also be used in sets it considers the end points of the intervals, if the
	 * start points are the same. Otherwise the set will not be able to handle two different intervals, sharing
	 * the same starting point, and omit one of the intervals.
	 *
	 * Since this is a static method of a generic class, it involves unchecked calls to class methods. It is left to
	 * ths user to ensure that she compares intervals from the same class, otherwise an exception might be thrown.
	 */
	public static Comparator<Interval> startComparator = new Comparator<Interval>() {
		@Override
		public int compare(Interval a, Interval b) {
			int compare = a.compareStarts(b);
			return compare != 0 ? compare : a.compareEnds(b);
		}
	};

	/**
	 * A comparator that can be used as a parameter for sorting functions. The end comparator sorts the intervals
	 * in <em>descending</em> order by placing the intervals with a greater end point before intervals with smaller
	 * end points. This corresponds to a line sweep from right to left.
	 *
	 * Intervals with end point null (positive infinity) are placed before all other intervals. If two intervals
	 * have the same end point, the closed end point is placed before the open one. For example,  [0, 10) is placed
	 * after (0, 10].
	 *
	 * To ensure that this comparator can also be used in sets it considers the start points of the intervals, if the
	 * end points are the same. Otherwise the set will not be able to handle two different intervals, sharing
	 * the same end point, and omit one of the intervals.
	 *
	 * Since this is a static method of a generic class, it involves unchecked calls to class methods. It is left to
	 * ths user to ensure that she compares intervals from the same class, otherwise an exception might be thrown.
	 */
	public static Comparator<Interval> endComparator = new Comparator<Interval>() {
		@Override
		public int compare(Interval a, Interval b) {
			int compare = b.compareEnds(a);
			return compare != 0 ? compare : b.compareStarts(a);
		}
	};

	@Override
	public int hashCode() {
		int prime = 31;
		int result = start == null ? 0 : start.hashCode();
		result = prime * result +(end == null ? 0 : end.hashCode());
		result = prime * result + (isStartInclusive ? 1 : 0);
		result = prime * result + (isEndInclusive ? 1 : 0);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().isAssignableFrom(obj.getClass()))
			return false;
		Interval other = (Interval)obj;
		return startComparator.compare(this, other) == 0 && endComparator.compare(this, other) == 0;
	}
}
