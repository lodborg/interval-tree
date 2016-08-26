package com.lodborg.intervaltree;

import java.util.ArrayList;
import java.util.List;

public class IntervalTree<T extends Comparable<? super T>> {
	TreeNode<T> root;

	public void addInterval(Interval<T> interval){
		if (interval.isEmpty())
			return;
		root = TreeNode.addInterval(root, interval);
	}

	public List<Interval<T>> query(T point){
		return TreeNode.query(root, point, new ArrayList<>());
	}

	public void removeInterval(Interval<T> interval){
		if (interval.isEmpty() || root == null)
			return;
		root = TreeNode.removeInterval(root, interval);
	}
}