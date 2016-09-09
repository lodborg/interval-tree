package com.lodborg.intervaltree;

import java.util.*;

/**
 * A representation of a single node in the {@link IntervalTree}.
 *<p>
 * Since the Interval Tree is practically a binary search tree, every node is
 * identified by a unique key - the center (called middlepoint) of the first
 * interval that triggers the creation of this node. The key is immutable and
 * can't be changed during the lifespan of this node. It can however move up or
 * down in the tree as a result of balancing rotations, similarly to a normal
 * balanced search tree. Unlike a traditional binary search tree, every node in
 * the interval tree can store multiple intervals. The invariant is, that each node
 * stores only intervals that contain its middlepoint.
 * </p>
 * <p>
 * There are several invariants that the {@code TreeNode} needs to keep. First,
 * the subtrees rooted in each node are always kept balanced. Second, each node
 * must contain at least one interval, otherwise the node must be removed from
 * the tree to keep it as small as possible. Balancing is done by traditional
 * binary search tree rotations.
 * </p>
 * <p>
 * Given an interval that needs to be found, inserted or removed from the tree,
 * an augmented binary search algorithm is performed in the following fashion.
 * For each visited node, check if the middlepoint of this node is contained in
 * the query interval. If so, then this node is the position where the query interval
 * must be stored. Otherwise, visit the left child, if the interval is entirely
 * to the left of the middlepoint, or visit the right child otherwise.
 * </p>
 * <p>
 * Since intervals may contain middlepoints from multiple nodes in the tree,
 * there are multiple valid locations where an interval may be stored based
 * on the above augmented binary search algorithm. However, to ensure the
 * optimal time complexity of the {@link IntervalTree#add(Interval) add},
 * {@link IntervalTree#remove(Object) remove} and
 * {@link IntervalTree#contains(Object) contains} operations, we define the base
 * position of an interval (or base) as the node closest to root within the set of
 * nodes, where the interval may be stored. To be able to guarantee the
 * correctness of the algorithm, this invariant must be preserved after each
 * tree operation. This is especially important, when tree rotations are
 * performed. Whenever a node is promoted closer to the root as a result of a
 * rotation, the promoted node must assimilate all intervals from the node demoted
 * by the rotation. This ensures that whenever the binary search algorithm encounters
 * a node, which is a valid location for a given interval, this node will be the base
 * of the interval.
 * </p>
 * <p>
 * Since each node can store multiple intervals, we need an efficient way
 * to perform intersecting queries (given a query in the form of a point or
 * an interval, find all intervals intersecting the query). The {@code TreeNode}
 * can't simply keep an ArrayList of all intervals stored in it, because
 * it will have to linearly check each interval in the List. This can
 * degrade the performance of the tree, especially in cases where a single
 * node contains all intervals. Intervals have to be stored in an ordered
 * way. Each node keeps its intervals in two {@link TreeSet}s at the same
 * time - one containing the intervals by their start points in ascending order
 * and the other by the end points in descending order. Whenever we have to provide
 * a set of all intervals contained within this node, that intersect a query point,
 * we check if the query is left or right of the middlepoint of the node.
 * If it is left, we iterate through the TreeSet ordered by the start points
 * until we reach an interval completely to the right of the query. Analogously,
 * if the query is to the right of the middlepoint, we iterate through the set
 * ordered by the end points until we reach an interval that's completely to
 * the left of the query. This allows us to only iterate through these intervals,
 * that we will actually return as a result of the query.
 * </p>
 *
 * @param <T> The type for the start and end point of the interval
 */
public class TreeNode<T extends Comparable<? super T>> implements Iterable<Interval<T>> {
	protected NavigableSet<Interval<T>> decreasing, increasing;
	protected volatile TreeNode<T> left, right;
	protected T midpoint;
	protected int height;

	public TreeNode(Interval<T> interval){
		decreasing = new TreeSet<>(Interval.sweepRightToLeft);
		increasing = new TreeSet<>(Interval.sweepLeftToRight);

		decreasing.add(interval);
		increasing.add(interval);
		midpoint = interval.getMidpoint();
		height = 1;
	}

	public static <T extends Comparable<? super T>> TreeNode<T> addInterval(IntervalTree<T> tree, TreeNode<T> root, Interval<T> interval) {
		if (root == null) {
			tree.size++;
			return new TreeNode<>(interval);
		}
		if (interval.contains(root.midpoint)){
			if (root.decreasing.add(interval))
				tree.size++;
			root.increasing.add(interval);
			return root;
		} else if (interval.isLeftOf(root.midpoint)){
			root.left = addInterval(tree, root.left, interval);
			root.height = Math.max(height(root.left), height(root.right))+1;
		} else {
			root.right = addInterval(tree, root.right, interval);
			root.height = Math.max(height(root.left), height(root.right))+1;
		}

		return root.balanceOut();
	}

	public int height(){
		return height;
	}

	private static int height(TreeNode node){
		return node == null ? 0 : node.height();
	}

	private TreeNode<T> balanceOut(){
		int balance = height(left) - height(right);
		if (balance < -1){
			// The tree is right-heavy.
			if (height(right.left) > height(right.right)){
				this.right = this.right.rightRotate();
				return leftRotate();
			} else{
				return leftRotate();
			}
		} else if (balance > 1){
			// The tree is left-heavy.
			if (height(left.right) > height(left.left)){
				this.left = this.left.leftRotate();
				return rightRotate();
			} else
				return rightRotate();
		} else {
			// The tree is already balanced.
			return this;
		}
	}

	private TreeNode<T> leftRotate(){
		TreeNode<T> head = right;
		right = head.left;
		head.left = this;
		height = Math.max(height(right), height(left)) + 1;
		head.left = head.assimilateOverlappingIntervals(this);
		return head;
	}

	private TreeNode<T> rightRotate(){
		TreeNode<T> head = left;
		left = head.right;
		head.right = this;
		height = Math.max(height(right), height(left)) + 1;
		head.right = head.assimilateOverlappingIntervals(this);
		return head;
	}

	private TreeNode<T> assimilateOverlappingIntervals(TreeNode<T> from) {
		ArrayList<Interval<T>> tmp = new ArrayList<>();

		if (midpoint.compareTo(from.midpoint) < 0){
			for (Interval<T> next: from.increasing){
				if (next.isRightOf(midpoint))
					break;
				tmp.add(next);
			}
		} else {
			for (Interval<T> next: from.decreasing){
				if (next.isLeftOf(midpoint))
					break;
				tmp.add(next);
			}
		}

		from.increasing.removeAll(tmp);
		from.decreasing.removeAll(tmp);
		increasing.addAll(tmp);
		decreasing.addAll(tmp);
		if (from.increasing.size() == 0){
			return deleteNode(from);
		}
		return from;
	}

	public static <T extends Comparable<? super T>> Set<Interval<T>> query(TreeNode<T> root, T point, Set<Interval<T>> res) {
		if (root == null)
			return res;
		if (point.compareTo(root.midpoint) <= 0){
			for (Interval<T> next: root.increasing){
				if (next.isRightOf(point))
					break;
				res.add(next);
			}
			return TreeNode.query(root.left, point, res);
		} else{
			for (Interval<T> next: root.decreasing){
				if (next.isLeftOf(point))
					break;
				res.add(next);
			}
			return TreeNode.query(root.right, point, res);
		}
	}

	public static <T extends Comparable<? super T>> TreeNode<T> removeInterval(IntervalTree<T> tree, TreeNode<T> root, Interval<T> interval) {
		if (root == null)
			return null;
		if (interval.contains(root.midpoint)){
			if (root.decreasing.remove(interval))
				tree.size--;
			root.increasing.remove(interval);
			if (root.increasing.size() == 0){
				return deleteNode(root);
			}

		} else if (interval.isLeftOf(root.midpoint)){
			root.left = removeInterval(tree, root.left, interval);
		} else {
			root.right = removeInterval(tree, root.right, interval);
		}
		return root.balanceOut();
	}

	private static <T extends Comparable<? super T>> TreeNode<T> deleteNode(TreeNode<T> root) {
		if (root.left == null && root.right == null)
			return null;

		if (root.left == null){
			// If the left child is empty, then the right subtree can consist of at most
			// one node, otherwise it would have been unbalanced. So, just return
			// the right child.
			return root.right;
		} else {
			TreeNode<T> node = root.left;
			Stack<TreeNode<T>> stack = new Stack<>();
			while (node.right != null){
				stack.push(node);
				node = node.right;
			}
			if (!stack.isEmpty()) {
				stack.peek().right = node.left;
				node.left = root.left;
			}
			node.right = root.right;

			TreeNode<T> newRoot = node;
			while (!stack.isEmpty()){
				node = stack.pop();
				if (!stack.isEmpty())
					stack.peek().right = newRoot.assimilateOverlappingIntervals(node);
				else
					newRoot.left = newRoot.assimilateOverlappingIntervals(node);
			}
			return newRoot.balanceOut();
		}
	}

	/**
	 * A helper method for the range search used in the interval intersection query in the tree.
	 * This corresponds to the left branch of the range search, once we find a node, whose
	 * midpoint is contained in the query interval. All intervals in the left subtree of that node
	 * are guaranteed to intersect with the query, if they have an endpoint greater or equal than
	 * the start of the query interval. Basically, this means that every time we branch to the left
	 * in the binary search, we need to add the whole right subtree to the result set.
	 *
	 * @param node    The left child of the node, whose midpoint is contained in the query interval.
	 * @param query   The query interval.
	 * @param result  The set which stores all intervals in the tree, intersecting the query.
	 */
	static <T extends Comparable<? super T>> void rangeQueryLeft(TreeNode<T> node, Interval<T> query, Set<Interval<T>> result) {
		while (node != null) {
			if (query.contains(node.midpoint)) {
				result.addAll(node.increasing);
				if (node.right != null) {
					for (Interval<T> next : node.right)
						result.add(next);
				}
				node = node.left;
			} else {
				for (Interval<T> next: node.decreasing){
					if (next.isLeftOf(query))
						break;
					result.add(next);
				}
				node = node.right;
			}
		}
	}

	/**
	 * A helper method for the range search used in the interval intersection query in the tree.
	 * This corresponds to the right branch of the range search, once we find a node, whose
	 * midpoint is contained in the query interval. All intervals in the right subtree of that node
	 * are guaranteed to intersect with the query, if they have an endpoint smaller or equal than
	 * the end of the query interval. Basically, this means that every time we branch to the right
	 * in the binary search, we need to add the whole left subtree to the result set.
	 *
	 * @param node    The right child of the node, whose midpoint is contained in the query interval.
	 * @param query   The query interval.
	 * @param result  The set which stores all intervals in the tree, intersecting the query.
	 */
	static <T extends Comparable<? super T>> void rangeQueryRight(TreeNode<T> node, Interval<T> query, Set<Interval<T>> result) {
		while (node != null) {
			if (query.contains(node.midpoint)) {
				result.addAll(node.increasing);
				if (node.left != null) {
					for (Interval<T> next : node.left)
						result.add(next);
				}
				node = node.right;
			} else {
				for (Interval<T> next: node.increasing){
					if (next.isRightOf(query))
						break;
					result.add(next);
				}
				node = node.left;
			}
		}
	}


	/**
	 * An iterator over all intervals stored in subtree rooted at the current node. Traversal
	 * is done via classic iterative in-order tree traversal where each iteration is in
	 * amortized O(1) time. The iterator requires O(logn) space - at each point of the
	 * traversal we keep a stack of the currently traversed branch of the tree.
	 */
	@Override
	public TreeNodeIterator iterator() {
		return new TreeNodeIterator();
	}

	class TreeNodeIterator implements Iterator<Interval<T>>{
		Stack<TreeNode<T>> stack = new Stack<>();
		TreeNode<T> subtreeRoot = TreeNode.this;
		TreeNode<T> currentNode;
		Interval<T> currentInterval;
		Iterator<Interval<T>> iterator = Collections.emptyIterator();

		@Override
		public boolean hasNext() {
			return subtreeRoot != null || !stack.isEmpty() || iterator.hasNext();
		}

		@Override
		public Interval<T> next() {
			if (!iterator.hasNext()) {
				while (subtreeRoot != null) {
					stack.push(subtreeRoot);
					subtreeRoot = subtreeRoot.left;
				}
				if (stack.isEmpty())
					throw new NoSuchElementException();
				currentNode = stack.pop();
				iterator = currentNode.increasing.iterator();
				subtreeRoot = currentNode.right;
			}
			currentInterval = iterator.next();
			return currentInterval;
		}

		@Override
		public void remove() {
			iterator.remove();
		}
	}
}
