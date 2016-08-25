package com.lodborg.intervaltree;

import com.lodborg.intervaltree.IntegerInterval;
import com.lodborg.intervaltree.Interval;

import java.util.ArrayList;
import java.util.List;

public class IntervalTree<T extends Comparable<? super T>> {
	TreeNode<T> root;

	public void addInterval(Interval<T> interval){
		if (interval.isEmpty())
			return;

		if (root == null){
			root = new TreeNode<>(interval);
		} else {
			root = root.addInterval(interval);
		}
	}

	public List<Interval<T>> query(Interval<T> point){
		List<Interval<T>> res = new ArrayList<>();
		return TreeNode.query(root, point, res);
	}

	public void removeInterval(Interval<T> interval){
		if (interval.isEmpty() || root == null)
			return;
		root = TreeNode.removeInterval(root, interval);
	}
}