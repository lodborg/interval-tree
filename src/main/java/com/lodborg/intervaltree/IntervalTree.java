package com.lodborg.intervaltree;

import java.util.*;
import com.lodborg.intervaltree.TreeNode.*;

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
			final TreeNodeIterator it = root.iterator();
			return new Iterator<Interval<T>>() {
				@Override
				public void remove() {
					if (it.currentNode.increasing.size() == 1){
						root = TreeNode.removeInterval(root, it.currentInterval);

						// Rebuild the whole branch stack in the iterator, because we might
						// have moved nodes around and introduced new nodes. The rule is,
						// add all nodes to the branch stack, to which the current node is
						// a left child.
						TreeNode<T> node = root;
						it.stack = new Stack<>();

						// Continue pushing elements according to the aforementioned rule until
						// you reach the subtreeRoot - this is the root of the subtree, which
						// the iterator has marked for traversal next. This subtree must not
						// become a part of the branch stack, or otherwise you will iterate over
						// some intervals twice.
						while (node != it.subtreeRoot){
							if (it.currentNode.midpoint.compareTo(node.midpoint) < 0) {
								it.stack.push(node);
								node = node.left;
							}
							else {
								node = node.right;
							}
						}
					} else {
						it.remove();
					}
				}

				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public Interval<T> next() {
					return it.next();
				}
			};
		}
	}
}