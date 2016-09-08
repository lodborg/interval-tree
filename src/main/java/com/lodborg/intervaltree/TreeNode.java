package com.lodborg.intervaltree;

import java.util.*;

public class TreeNode<T extends Comparable<? super T>> implements Iterable<Interval<T>>{
	protected NavigableSet<Interval<T>> decreasing, increasing;
	protected volatile TreeNode<T> left, right;
	protected T midpoint;
	protected volatile int height;

	public TreeNode(Interval<T> interval){
		decreasing = new TreeSet<>(Interval.endComparator);
		increasing = new TreeSet<>(Interval.startComparator);

		decreasing.add(interval);
		increasing.add(interval);
		midpoint = interval.getMidpoint();
		height = 1;
	}

	public static <T extends Comparable<? super T>> TreeNode<T> addInterval(TreeNode<T> root, Interval<T> interval) {
		if (root == null)
			return new TreeNode<>(interval);
		if (interval.contains(root.midpoint)){
			root.decreasing.add(interval);
			root.increasing.add(interval);
			return root;
		} else if (interval.isLeftOf(root.midpoint)){
			root.left = addInterval(root.left, interval);
			root.height = Math.max(height(root.left), height(root.right))+1;
		} else {
			root.right = addInterval(root.right, interval);
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

	public static <T extends Comparable<? super T>> TreeNode<T> removeInterval(TreeNode<T> root, Interval<T> interval) {
		if (root == null)
			return null;
		if (interval.contains(root.midpoint)){
			root.decreasing.remove(interval);
			root.increasing.remove(interval);
			if (root.increasing.size() == 0){
				return deleteNode(root);
			}

		} else if (interval.isLeftOf(root.midpoint)){
			root.left = removeInterval(root.left, interval);
		} else {
			root.right = removeInterval(root.right, interval);
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
	 * An iterator over all intervals stored in the tree. Traversal is done via classic
	 * iterative in-order tree traversal where each iteration is in amortized O(1) time.
	 * The iterator requires O(logn) space - at each point of the traversal we keep a
	 * stack of the currently traversed branch of the tree.
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
