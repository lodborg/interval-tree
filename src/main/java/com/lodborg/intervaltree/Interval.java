package com.lodborg.intervaltree;

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

	protected abstract Interval<T> create(T start, boolean isStartInclusive, T end, boolean isEndInclusive);

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

	abstract boolean isEmpty();

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
		if (intersection == null)
			return false;
		if ((intersection.start == null ^ another.start == null) || (intersection.start != null && !intersection.start.equals(another.start)))
			return false;
		if ((intersection.end == null ^ another.end == null) || (intersection.end != null && !intersection.end.equals(another.end)))
			return false;
		return intersection.isEndInclusive == another.isEndInclusive && intersection.isStartInclusive == another.isStartInclusive;
	}

	public boolean intersects(Interval<T> query){
		if (query == null)
			return false;
		Interval<T> intersection = getIntersection(query);
		return intersection != null && !intersection.isEmpty();
	}
}
