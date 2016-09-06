package com.lodborg.intervaltree;

import java.util.*;

public class IntervalTree<T extends Comparable<? super T>> {
	TreeNode<T> root;
	TreeMap<T, HashSet<Interval<T>>> endPointsMap = new TreeMap<>();

	public void addInterval(Interval<T> interval){
		if (interval.isEmpty())
			return;
		if (interval.getStart() != null) {
			HashSet<Interval<T>> set = endPointsMap.get(interval.getStart());
			if (set == null){
				set = new HashSet<>();
				endPointsMap.put(interval.getStart(), set);
			}
			set.add(interval);
		}
		if (interval.getEnd() != null) {
			HashSet<Interval<T>> set = endPointsMap.get(interval.getEnd());
			if (set == null){
				set = new HashSet<>();
				endPointsMap.put(interval.getEnd(), set);
			}
			set.add(interval);
		}
		root = TreeNode.addInterval(root, interval);
	}

	public Set<Interval<T>> query(T point){
		return TreeNode.query(root, point, new HashSet<Interval<T>>());
	}

	public Set<Interval<T>> query(Interval<T> interval){
		Set<Interval<T>> result = new HashSet<>();
		Set<Map.Entry<T, HashSet<Interval<T>>>> entries = endPointsMap.subMap(
				interval.getStart(),
				interval.isStartInclusive(),
				interval.getEnd(),
				interval.isEndInclusive()
		).entrySet();

		for (Map.Entry<T, HashSet<Interval<T>>> entry: entries){
			for (Interval<T> next: entry.getValue()){
				if (next.intersects(interval))
					result.add(next);
			}
		}
		return TreeNode.query(root, interval.getMidpoint(), result);
	}

	public void removeInterval(Interval<T> interval){
		if (interval.isEmpty() || root == null)
			return;
		if (interval.getStart() != null){
			HashSet<Interval<T>> set = endPointsMap.get(interval.getStart());
			if (set != null) {
				set.remove(interval);
				if (set.isEmpty())
					endPointsMap.remove(interval.getStart());
			}
		}
		if (interval.getEnd() != null){
			HashSet<Interval<T>> set = endPointsMap.get(interval.getEnd());
			if (set != null) {
				set.remove(interval);
				if (set.isEmpty())
					endPointsMap.remove(interval.getEnd());
			}
		}
		root = TreeNode.removeInterval(root, interval);
	}
}