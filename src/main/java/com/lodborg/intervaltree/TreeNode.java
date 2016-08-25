package com.lodborg.intervaltree;

import com.lodborg.intervaltree.Interval;

import java.util.*;

public class TreeNode<T extends Comparable<? super T>> {
	TreeSet<Interval<T>> decreasing, increasing;
	TreeNode<T> left, right;
	T midpoint;
	int height;

	Comparator<Interval<T>> c1 = new Comparator<Interval<T>>() {
		@Override
		public int compare(Interval<T> a, Interval<T> b) {
			int compare = b.compareEnds(a);
			return compare != 0 ? compare : b.compareStarts(a);
		}
	};

	Comparator<Interval<T>> c2 = new Comparator<Interval<T>>() {
		@Override
		public int compare(Interval<T> a, Interval<T> b) {
			int compare = a.compareStarts(b);
			return compare != 0 ? compare : a.compareEnds(b);
		}
	};

	public TreeNode(Interval<T> interval){
		decreasing = new TreeSet<>(c1);
		increasing = new TreeSet<>(c2);
		/*decreasing = new TreeSet<>((a, b) -> {
			if (a.getEnd() == null)
				return -1;
			if (b.getEnd() == null)
				return 1;
			int compare = b.getEnd().compareTo(a.getEnd());
			if (compare != 0)
				return compare;
			if (a.isEndInclusive() ^ b.isEndInclusive())
				return a.isEndInclusive() ? -1 : 1;
			return 0;
		});
		increasing = new TreeSet<>((a, b) -> {
			if (a.getStart() == null)
				return -1;
			if (b.getStart() == null)
				return 1;
			int compare = a.getStart().compareTo(b.getStart());
			if (compare != 0)
				return compare;
			if (a.isStartInclusive() ^ b.isStartInclusive())
				return a.isStartInclusive() ? -1 : 1;
			return 0;
		});*/
		decreasing.add(interval);
		increasing.add(interval);
		midpoint = interval.getMidpoint();
		height = 1;
	}

	public TreeNode<T> addInterval(Interval<T> interval){
		if (interval.contains(midpoint)){
			decreasing.add(interval);
			increasing.add(interval);
		} else if (interval.isLeftOf(midpoint)){
			if (left == null)
				left = new TreeNode<>(interval);
			else
				left = left.addInterval(interval);
			height = Math.max(height(left), height(right))+1;
		} else {
			if (right == null)
				right = new TreeNode<>(interval);
			else
				right = right.addInterval(interval);
			height = Math.max(height(left), height(right))+1;
		}

		return balanceOut();
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
		if (from.increasing.size() == 0)
			return deleteNode(from);
		Interval.Builder ref = from.increasing.first().getBuilder();
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


	public static <T extends Comparable<? super T>> List<Interval<T>> query(TreeNode<T> root, Interval<T> point, List<Interval<T>> res) {
		if (root == null)
			return res;
		if (point.isLeftOf(root.midpoint)){
			for (Interval<T> next: root.increasing){
				if (point.compareStarts(next) < 0)
					break;
				res.add(next);
			}
			return TreeNode.query(root.left, point, res);
		} else{
			for (Interval<T> next: root.decreasing){
				if (point.compareEnds(next) > 0)
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
		if (root == null)
			return null;
		if (root.left == null && root.right == null)
			return null;
		if (root.left == null){
			TreeNode<T> node = root.right;
			TreeNode<T> parent = null;
			while (node.left != null){
				parent = node;
				node = node.left;
			}
			if (parent != null) {
				parent.left = node.right;
				node.right = root.right;
			}

			TreeNode<T> newRoot = node;
			node = node.right;
			while (node != null){
				newRoot.assimilateOverlappingIntervals(node);
				node = node.left;
			}
			return newRoot.balanceOut();
		} else {
			TreeNode<T> node = root.left;
			TreeNode<T> parent = null;
			while (node.right != null){
				parent = node;
				node = node.right;
			}
			if (parent != null) {
				parent.right = node.left;
				node.left = root.left;
			}
			node.right = root.right;

			TreeNode<T> newRoot = node;
			node = node.left;
			while (node != null){
				newRoot.assimilateOverlappingIntervals(node);
				node = node.right;
			}
			return newRoot.balanceOut();
		}
	}
}
