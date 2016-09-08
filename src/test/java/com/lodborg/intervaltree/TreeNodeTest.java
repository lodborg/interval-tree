package com.lodborg.intervaltree;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import com.lodborg.intervaltree.Interval.*;

import java.util.*;

public class TreeNodeTest {
	@Test
	public void test_iteratorNormal(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer>[] arr = new IntegerInterval[]{
				new IntegerInterval(6, 10, Bounded.OPEN),
				new IntegerInterval(2, 100, Bounded.CLOSED),
				new IntegerInterval(8, 20, Bounded.CLOSED),
				new IntegerInterval(-2, 0, Bounded.CLOSED),
				new IntegerInterval(-3, 0, Bounded.CLOSED_RIGHT),
				new IntegerInterval(10, 20, Bounded.CLOSED_LEFT),
				new IntegerInterval(11, 14, Bounded.CLOSED_LEFT),
				new IntegerInterval(-20, -10, Bounded.CLOSED),
				new IntegerInterval(-14, -11, Bounded.CLOSED),
				new IntegerInterval(-14, -10, Bounded.OPEN),
				new IntegerInterval(0, 4, Bounded.OPEN)
		};
		for (Interval<Integer> interval: arr)
			tree.add(interval);
		List<Interval<Integer>> list = new ArrayList<>();
		for (Interval<Integer> interval: tree)
			list.add(interval);

		assertThat(new HashSet<>(Arrays.asList(arr)), is(new HashSet<>(list)));
		assertEquals(arr.length, list.size());
	}

	@Test
	public void test_iteratorEmpty(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		assertFalse(tree.iterator().hasNext());
		tree.add(new IntegerInterval(1, 3, Bounded.CLOSED));
		Iterator<Interval<Integer>> it = tree.iterator();
		it.next();
		assertFalse(it.hasNext());
		try{
			it.next();
			fail();
		} catch (Exception e){
			assertTrue(e instanceof NoSuchElementException);
		}
	}

	@Test
	public void test_iteratorBackToRootWithMultipleIntervals(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Set<Interval<Integer>> set = new HashSet<>(new ArrayList<Interval<Integer>>(Arrays.asList(
				new IntegerInterval(12, 22, Bounded.CLOSED_RIGHT),
				new IntegerInterval(12, 25, Bounded.CLOSED_RIGHT),
				new IntegerInterval(0, 10, Bounded.CLOSED_RIGHT)
		)));
		for (Interval<Integer> next: set)
			tree.add(next);
		Set<Interval<Integer>> result = new HashSet<>();
		for (Interval<Integer> next: tree)
			result.add(next);
		assertThat(set, is(result));
	}

	@Test
	public void test_iteratorRemove(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer> target = new IntegerInterval(12, 22, Bounded.CLOSED_RIGHT);
		Interval<Integer> root = new IntegerInterval(2, 10, Bounded.CLOSED_LEFT);
		Interval<Integer> left = new IntegerInterval(1, 5, Bounded.OPEN);
		tree.add(root);
		tree.add(target);
		tree.add(left);
		Iterator<Interval<Integer>> it = tree.iterator();
		while (it.hasNext()){
			Interval<Integer> next = it.next();
			if (next == target)
				it.remove();
		}

		List<Interval<Integer>> list = new ArrayList<>();
		for (Interval<Integer> next: tree)
			list.add(next);

		assertTrue(list.contains(root));
		assertTrue(list.contains(left));
		assertFalse(list.contains(target));
	}

	@Test
	public void test_iteratorRemoveChangesTheRoot(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer> target = new IntegerInterval(12, 22, Bounded.CLOSED_RIGHT);
		Interval<Integer> root = new IntegerInterval(2, 10, Bounded.CLOSED_LEFT);
		Interval<Integer> left = new IntegerInterval(1, 5, Bounded.OPEN);
		Interval<Integer> leftGrandchild = new IntegerInterval(-10, 0, Bounded.OPEN);
		tree.add(root);
		tree.add(target);
		tree.add(left);
		tree.add(leftGrandchild);
		TreeNode<Integer> newRoot = tree.root.left;

		Iterator<Interval<Integer>> it = tree.iterator();
		while (it.hasNext()){
			Interval<Integer> next = it.next();
			if (next == target)
				it.remove();
		}

		List<Interval<Integer>> list = new ArrayList<>();
		for (Interval<Integer> next: tree)
			list.add(next);

		assertTrue(list.contains(root));
		assertTrue(list.contains(left));
		assertTrue(list.contains(leftGrandchild));
		assertFalse(list.contains(target));

		assertEquals(newRoot, tree.root);
	}

	@Test
	public void test_iteratorRemoveIntervalWithoutDeletingNode(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval[] arr = new IntegerInterval[]{
				new IntegerInterval(20, 30, Bounded.CLOSED_LEFT),
				new IntegerInterval(0, 10, Bounded.CLOSED_RIGHT),
				new IntegerInterval(30, 40, Bounded.OPEN)
		};
		IntegerInterval target = new IntegerInterval(-4, 18, Bounded.CLOSED);
		for (Interval<Integer> next: arr)
			tree.add(next);
		tree.add(target);

		TreeNode<Integer> root = tree.root;
		TreeNode<Integer> left = root.left;
		TreeNode<Integer> right = root.right;

		Iterator<Interval<Integer>> it = tree.iterator();
		while(it.hasNext()){
			Interval<Integer> next = it.next();
			if (next == target)
				it.remove();
		}

		assertEquals(tree.root, root);
		assertEquals(tree.root.left, left);
		assertEquals(tree.root.right, right);

		List<Interval<Integer>> list = new ArrayList<>();
		for (Interval<Integer> next: tree){
			list.add(next);
		}

		for (Interval<Integer> next: arr)
			assertTrue(list.contains(next));
		assertFalse(list.contains(target));
	}

	@Test
	public void test_iteratorRemoveDeletesInnerNode(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval target = new IntegerInterval(40, 50, Bounded.OPEN);
		IntegerInterval[] arr = new IntegerInterval[]{
				new IntegerInterval(100, 110, Bounded.CLOSED_LEFT),
				new IntegerInterval(150, 160, Bounded.CLOSED_LEFT),
				new IntegerInterval(50, 60, Bounded.CLOSED_RIGHT),
				new IntegerInterval(160, 170, Bounded.CLOSED_LEFT),
				new IntegerInterval(60, 70, Bounded.OPEN),
				target,
				new IntegerInterval(70, 80, Bounded.OPEN),
				new IntegerInterval(140, 150, Bounded.CLOSED_LEFT),

		};
		for (IntegerInterval next: arr)
			tree.add(next);
		Iterator<Interval<Integer>> it = tree.iterator();
		List<Interval<Integer>> list = new ArrayList<>();
		while(it.hasNext()){
			Interval<Integer> next = it.next();
			if (next == target)
				it.remove();
			else
				list.add(next);
		}

		assertEquals(arr.length-1, list.size());
		for (Interval<Integer> next: arr){
			if (target == next)
				assertFalse(list.contains(next));
			else
				assertTrue(list.contains(next));
		}
	}

	@Test
	public void test_iteratorRemoveDeletesInnerNodeAndPromotesTheSubtreeRoot(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval target = new IntegerInterval(40, 50, Bounded.OPEN);
		IntegerInterval[] arr = new IntegerInterval[]{
				new IntegerInterval(100, 110, Bounded.CLOSED_LEFT),
				new IntegerInterval(150, 160, Bounded.CLOSED_LEFT),
				new IntegerInterval(50, 60, Bounded.CLOSED_RIGHT),
				new IntegerInterval(160, 170, Bounded.CLOSED_LEFT),
				new IntegerInterval(60, 70, Bounded.OPEN),
				target,
				new IntegerInterval(70, 80, Bounded.OPEN),
				new IntegerInterval(140, 150, Bounded.CLOSED_LEFT),
				new IntegerInterval(46, 49, Bounded.CLOSED)

		};
		for (IntegerInterval next: arr)
			tree.add(next);
		Iterator<Interval<Integer>> it = tree.iterator();
		List<Interval<Integer>> list = new ArrayList<>();
		while(it.hasNext()){
			Interval<Integer> next = it.next();
			if (next == target)
				it.remove();
			else
				list.add(next);
		}

		assertEquals(arr.length-1, list.size());
		for (Interval<Integer> next: arr){
			if (target == next)
				assertFalse(list.contains(next));
			else
				assertTrue(list.contains(next));
		}
	}
}
