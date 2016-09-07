package com.lodborg.intervaltree;

import java.util.*;

public class IntervalTree<T extends Comparable<? super T>> implements Iterable<Interval<T>> {
	TreeNode<T> root;

	public void addInterval(Interval<T> interval){
		if (interval.isEmpty())
			return;
		root = TreeNode.addInterval(root, interval);
	}

	public Set<Interval<T>> query(T point){
		return TreeNode.query(root, point, new HashSet<Interval<T>>());
	}

	public Set<Interval<T>> query(Interval<T> interval){
		Set<Interval<T>> result = new HashSet<>();

		if (root == null || interval.isEmpty())
			return result;
		TreeNode<T> node = root;
		while (node != null){
			if (interval.contains(node.midpoint)){
				result.addAll(node.increasing);
				TreeNode.rangeQueryLeft(node.left, interval, result);
				TreeNode.rangeQueryRight(node.right, interval, result);
				break;
			}
			if (interval.isLeftOf(node.midpoint)) {
				for (Interval<T> next: node.increasing){
					if (!interval.intersects(next))
						break;
					result.add(next);
				}
				node = node.left;
			}
			else {
				for (Interval<T> next: node.decreasing){
					if (!interval.intersects(next))
						break;
					result.add(next);
				}
				node = node.right;
			}
		}
		return result;
	}

	public void removeInterval(Interval<T> interval){
		if (interval.isEmpty() || root == null)
			return;
		root = TreeNode.removeInterval(root, interval);
	}

	@Override
	public Iterator<Interval<T>> iterator() {
		if (root == null){
			return Collections.emptyIterator();
		}
		else {
			return root.iterator();
		}
	}
}