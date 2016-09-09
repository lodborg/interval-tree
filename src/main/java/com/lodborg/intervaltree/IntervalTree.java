package com.lodborg.intervaltree;

import java.util.*;
import com.lodborg.intervaltree.TreeNode.*;

/**
 * An implementation of a Centered Interval Tree for efficient search in a set of intervals. See
 * <a href="https://en.wikipedia.org/wiki/Interval_tree">https://en.wikipedia.org/wiki/Interval_tree</a>.
 *
 * <p>
 * The tree functions as a set, meaning that it will not store an interval more than
 * once. More formally, for any two distinct intervals x and y within the tree, it is
 * guaranteed that x.equals(y) will evaluate to false. If you try to add an interval to the tree,
 * which is already in it, the tree will reject it. See the documentation of
 * {@link #add(Interval) the add method} for more information. The tree will also <strong>not</strong> accept
 * {@code null} or empty intervals, meaning intervals whose {@link Interval#isEmpty()}
 * method evaluates to {@code true}.
 * </p>
 * <p>
 * The {@link #iterator()} method of the tree returns a fail-fast iterator, which will
 * throw a {@code ConcurrentModificationException}, if the tree is modified in any form
 * during the iteration, other than by using the iterator's own {@code remove} method. However,
 * this is done in a best-effort manner, since it is generally hard to guarantee this behaviour
 * while using non-atomic and not synchronized methods.
 *</p>
 * <p>
 * The tree relies on the usage of a subclass of the {@link Interval} class to represent the
 * intervals. The majority of the interval methods are already implemented within the
 * {@code Interval} class and don't have to be implemented by the extending class. Comparisons
 * between the intervals are also pre-implemented in the {@code Interval} class and use the
 * start and endpoints to create a total order of all stored intervals. However, if the tree
 * needs to store intervals that have the same start and end points but represent different
 * logical entities, you need a subclass that overwrites the {@code equals}, {@code hashCode}
 * and {@code compareTo} methods. See the documentation of {@link Interval} for more information.
 *</p>
 *
 * @param <T> The type for the start and end point of the interval
 */
public class IntervalTree<T extends Comparable<? super T>> extends AbstractSet<Interval<T>> {
	TreeNode<T> root;
	int size;

	@Override
	public boolean add(Interval<T> interval){
		if (interval.isEmpty())
			return false;
		int sizeBeforeOperation = size;
		root = TreeNode.addInterval(this, root, interval);
		return size == sizeBeforeOperation;
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

	public boolean remove(Interval<T> interval){
		if (interval.isEmpty() || root == null)
			return false;
		int sizeBeforeOperation = size;
		root = TreeNode.removeInterval(this, root, interval);
		return size == sizeBeforeOperation;
	}




	// =========================================================================
	// ============== Iterator over the Intervals in the tree ==================
	// =========================================================================

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
						root = TreeNode.removeInterval(IntervalTree.this, root, it.currentInterval);

						// Rebuild the whole branch stack in the iterator, because we might have
						// moved nodes around and introduced new nodes into the branch. The rule
						// is, add all nodes to the branch stack, to which the current node is
						// a left descendant.
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





	// =========================================================================
	// ================== Methods from the Set interface =======================
	// =========================================================================

	public int size(){
		return size;
	}

	@Override
	public void clear() {
		size = 0;
		root = null;
	}

	@Override
	public boolean contains(Object o) {
		if (root == null || o == null)
			return false;
		if (!(o instanceof Interval))
			return false;
		Interval<T> query;
		query = (Interval<T>)o;
		TreeNode<T> node = root;
		while (node != null){
			if (query.contains(node.midpoint)){
				return node.increasing.contains(query);
			}
			if (query.isLeftOf(node.midpoint)){
				node = node.left;
			} else {
				node = node.right;
			}
		}

		return false;
	}
}