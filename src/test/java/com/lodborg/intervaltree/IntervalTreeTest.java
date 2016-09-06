package com.lodborg.intervaltree;

import org.junit.Test;

import com.lodborg.intervaltree.Interval.*;

import java.util.*;

import static org.junit.Assert.*;

public class IntervalTreeTest {

	@Test
	public void test_deleteNodeAfterAssimilation(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer> a = new IntegerInterval(0, 100, Bounded.CLOSED);
		Interval<Integer> b = new IntegerInterval(30, 40, Bounded.CLOSED_LEFT);
		Interval<Integer> c = new IntegerInterval(10, 20, Bounded.CLOSED_RIGHT);
		tree.addInterval(a);
		tree.addInterval(b);
		tree.addInterval(c);
		assertNull(tree.root.right);
		assertTrue(tree.root.increasing.contains(a));
		assertTrue(tree.root.increasing.contains(b));
		assertTrue(tree.root.decreasing.contains(a));
		assertTrue(tree.root.decreasing.contains(b));
		assertEquals(2, tree.root.increasing.size());
		assertEquals(2, tree.root.decreasing.size());
		assertTrue(tree.root.left.decreasing.contains(c));
		assertTrue(tree.root.left.increasing.contains(c));
		assertEquals(1, tree.root.left.increasing.size());
	}

	@Test
	public void test_removeIntervalForcesRootDelete(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer> a = new IntegerInterval(20, 30, Bounded.CLOSED);
		Interval<Integer> b = new IntegerInterval(0, 10, Bounded.CLOSED_LEFT);
		Interval<Integer> c = new IntegerInterval(40, 50, Bounded.CLOSED_RIGHT);
		tree.addInterval(a);
		tree.addInterval(b);
		tree.addInterval(c);
		tree.removeInterval(a);
		assertNull(tree.root.left);
		assertFalse(tree.root.increasing.contains(a));
		assertFalse(tree.root.decreasing.contains(a));
		assertTrue(tree.root.decreasing.contains(b));
		assertTrue(tree.root.increasing.contains(b));
		assertTrue(tree.root.right.decreasing.contains(c));
		assertTrue(tree.root.right.increasing.contains(c));
		assertEquals(1, tree.root.decreasing.size());
		assertEquals(1, tree.root.increasing.size());
		assertEquals(0, tree.query(22).size());
	}

	@Test
	public void test_removeFromNodeWithEmptyLeftChild(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer> a = new IntegerInterval(20, 30, Bounded.CLOSED);
		Interval<Integer> b = new IntegerInterval(40, 50, Bounded.CLOSED_RIGHT);
		tree.addInterval(a);
		tree.addInterval(b);
		tree.removeInterval(a);
		assertNull(tree.root.left);
		assertNull(tree.root.right);
		assertFalse(tree.root.increasing.contains(a));
		assertFalse(tree.root.decreasing.contains(a));
		assertTrue(tree.root.decreasing.contains(b));
		assertTrue(tree.root.increasing.contains(b));
		assertEquals(1, tree.root.decreasing.size());
		assertEquals(1, tree.root.increasing.size());
	}

	@Test
	public void test_removeRootThenAssimilateIntervalsFromInnerNodeAfterBubbleUp(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer> a = new IntegerInterval(20, 30, Bounded.CLOSED);
		Interval<Integer> b = new IntegerInterval(0, 10, Bounded.CLOSED_LEFT);
		Interval<Integer> c = new IntegerInterval(40, 50, Bounded.CLOSED_RIGHT);
		Interval<Integer> d = new IntegerInterval(7, 9, Bounded.OPEN);
		tree.addInterval(a);
		tree.addInterval(b);
		tree.addInterval(c);
		tree.addInterval(d);
		tree.removeInterval(a);

		assertNull(tree.root.left);
		assertEquals(2, tree.root.increasing.size());
		assertEquals(2, tree.root.decreasing.size());
		assertTrue(tree.root.increasing.contains(b));
		assertTrue(tree.root.increasing.contains(d));
		assertTrue(tree.root.decreasing.contains(b));
		assertTrue(tree.root.decreasing.contains(d));
		assertTrue(tree.root.right.decreasing.contains(c));
		assertTrue(tree.root.right.increasing.contains(c));
	}

	@Test
	public void test_removeRootReplaceWithDeepNodeLeftAndAssimilateIntervals(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer> a = new IntegerInterval(0, 100, Bounded.CLOSED);
		Interval<Integer> b = new IntegerInterval(10, 30, Bounded.CLOSED_LEFT);
		Interval<Integer> c = new IntegerInterval(60, 70, Bounded.CLOSED_RIGHT);
		Interval<Integer> d = new IntegerInterval(80, 90, Bounded.OPEN);
		Interval<Integer> e = new IntegerInterval(0, 5, Bounded.OPEN);
		Interval<Integer> f = new IntegerInterval(25, 49, Bounded.OPEN);
		Interval<Integer> g = new IntegerInterval(39, 44, Bounded.OPEN);
		tree.addInterval(a);
		tree.addInterval(b);
		tree.addInterval(c);
		tree.addInterval(d);
		tree.addInterval(e);
		tree.addInterval(f);
		tree.addInterval(g);
		tree.removeInterval(a);

		assertNull(tree.root.left.right);
		assertTrue(tree.root.decreasing.contains(g));
		assertTrue(tree.root.decreasing.contains(f));
		assertTrue(tree.root.increasing.contains(g));
		assertTrue(tree.root.increasing.contains(f));
		assertFalse(tree.root.decreasing.contains(a));
		assertFalse(tree.root.increasing.contains(a));
	}

	@Test
	public void test_removeRootReplaceWithDeepAssimilatingAnotherInnerNode(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer> a = new IntegerInterval(0, 100, Bounded.CLOSED);
		Interval<Integer> b = new IntegerInterval(10, 40, Bounded.CLOSED_LEFT);
		Interval<Integer> c = new IntegerInterval(60, 70, Bounded.CLOSED_RIGHT);
		Interval<Integer> d = new IntegerInterval(80, 90, Bounded.OPEN);
		Interval<Integer> e = new IntegerInterval(0, 5, Bounded.OPEN);
		Interval<Integer> f = new IntegerInterval(25, 27, Bounded.OPEN);
		Interval<Integer> g = new IntegerInterval(37, 40, Bounded.OPEN);
		tree.addInterval(a);
		tree.addInterval(b);
		tree.addInterval(c);
		tree.addInterval(d);
		tree.addInterval(e);
		tree.addInterval(f);
		tree.addInterval(g);

		TreeNode<Integer> nodeF = tree.root.left.right;
		TreeNode<Integer> nodeG = tree.root.left.right.right;
		TreeNode<Integer> nodeE = tree.root.left.left;
		TreeNode<Integer> nodeC = tree.root.right;

		tree.removeInterval(a);

		assertTrue(tree.root == nodeG);
		assertTrue(tree.root.left == nodeF);
		assertTrue(nodeF.left == nodeE);
		assertNull(nodeF.right);
		assertTrue(nodeG.right == nodeC);
	}

	@Test
	public void test_addEmptyInterval(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval a = new IntegerInterval(5, 6, Bounded.OPEN);
		tree.addInterval(a);
		assertNull(tree.root);
	}

	@Test
	public void test_removeFromEmptyTree(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		tree.removeInterval(new IntegerInterval(10, 20, Bounded.CLOSED));
		assertNull(tree.root);
	}

	@Test
	public void test_removeEmptyInterval(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval a = new IntegerInterval(1, 4, Bounded.OPEN);
		tree.addInterval(a);
		tree.removeInterval(new IntegerInterval(30, 20, Bounded.CLOSED));
		assertEquals(1, tree.root.decreasing.size());
		assertEquals(1, tree.root.increasing.size());
		assertTrue(tree.root.decreasing.contains(a));
		assertTrue(tree.root.increasing.contains(a));
	}

	@Test
	public void test_removeNonExistingInterval(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer> a = new IntegerInterval(20, 30, Bounded.CLOSED);
		Interval<Integer> b = new IntegerInterval(0, 10, Bounded.CLOSED_LEFT);
		Interval<Integer> c = new IntegerInterval(40, 50, Bounded.CLOSED_RIGHT);
		tree.addInterval(a);
		tree.addInterval(b);
		tree.addInterval(c);
		tree.removeInterval(new IntegerInterval(10, 20, Bounded.CLOSED));
		assertTrue(tree.root.decreasing.contains(a));
		assertTrue(tree.root.increasing.contains(a));
		assertEquals(1, tree.root.decreasing.size());
		assertTrue(tree.root.left.increasing.contains(b));
		assertTrue(tree.root.left.decreasing.contains(b));
		assertEquals(1, tree.root.left.decreasing.size());
		assertTrue(tree.root.right.increasing.contains(c));
		assertTrue(tree.root.right.decreasing.contains(c));
		assertEquals(1, tree.root.right.decreasing.size());

		tree = new IntervalTree<>();
		tree.addInterval(a);
		tree.removeInterval(new IntegerInterval(10, 40, Bounded.OPEN));
		assertTrue(tree.root.decreasing.contains(a));
		assertTrue(tree.root.increasing.contains(a));
		assertEquals(1, tree.root.increasing.size());
		assertEquals(1, tree.root.decreasing.size());
	}

	@Test
	public void firstTest() {
		IntervalTree<Integer> tree = new IntervalTree<>();

		tree.addInterval(new IntegerInterval(10, 20, Bounded.CLOSED));
		tree.addInterval(new IntegerInterval(20, 40, Bounded.CLOSED_LEFT));
		tree.addInterval(new IntegerInterval(15, 35, Bounded.CLOSED));
		tree.addInterval(new IntegerInterval(-20, 15, Bounded.OPEN));
		tree.addInterval(new IntegerInterval(0, 16, Bounded.OPEN));
		tree.addInterval(new IntegerInterval(32, Unbounded.CLOSED_LEFT));
		tree.addInterval(new IntegerInterval(17, Unbounded.CLOSED_RIGHT));
		tree.addInterval(new IntegerInterval());

		assertEquals(5, tree.query(15).size());
		assertEquals(2, tree.query(-20).size());
		assertEquals(3, tree.query(-17).size());
		assertEquals(5, tree.query(11).size());
		assertEquals(3, tree.query(-8).size());

		tree = new IntervalTree<>();
		tree.addInterval(new IntegerInterval(10, 20, Bounded.OPEN));
		tree.addInterval(new IntegerInterval(10, 12, Bounded.OPEN));
		tree.addInterval(new IntegerInterval(-1000, 8, Bounded.CLOSED));

		assertEquals(2, tree.query(11).size());
		assertEquals(0, tree.query(9).size());
		assertEquals(1, tree.query(0).size());

		tree = new IntervalTree<>();
		tree.addInterval(new IntegerInterval(7450, Unbounded.OPEN_LEFT));
		tree.addInterval(new IntegerInterval(209, Unbounded.OPEN_RIGHT));
		tree.addInterval(new IntegerInterval(2774, Unbounded.CLOSED_RIGHT));

		assertEquals(1, tree.query(8659).size());

		tree = new IntervalTree<>();
		tree.addInterval(new IntegerInterval(6213, Unbounded.OPEN_RIGHT));
		tree.addInterval(new IntegerInterval(684, Unbounded.CLOSED_LEFT));
		tree.addInterval(new IntegerInterval(-4657, -4612, Bounded.OPEN));

		assertEquals(1, tree.query(359).size());

		tree = new IntervalTree<>();
		tree.addInterval(new IntegerInterval().create(8705, true, 9158, true));
		tree.addInterval(new IntegerInterval().create(8899, false, 8966, false));
		tree.addInterval(new IntegerInterval().create(-7200, true, null, true));
		tree.addInterval(new IntegerInterval().create(315, false, 408, true));
		tree.addInterval(new IntegerInterval().create(965, false, 1218, true));

		assertEquals(2, tree.query(9042).size());

		tree = new IntervalTree<>();
		tree.addInterval(new IntegerInterval().create(2988, true, 3362, false));
		assertEquals(1, tree.query(2988).size());

		tree = new IntervalTree<>();
		tree.addInterval(new IntegerInterval().create(8457, true, 8926, true));
		tree.addInterval(new IntegerInterval().create(2988, true, 3362, false));
		tree.addInterval(new IntegerInterval().create(null, true, -523, true));
		tree.addInterval(new IntegerInterval().create(-5398, false, -5250, true));
		tree.addInterval(new IntegerInterval().create(-2912, false, -2727, true));
		assertEquals(1, tree.query(2988).size());

		tree = new IntervalTree<>();
		tree.addInterval(new IntegerInterval().create(91449, true, 91468, true));
		tree.addInterval(new IntegerInterval().create(-74038, false, -74037, true));
		tree.addInterval(new IntegerInterval().create(-53053, false, null, true));
		List<Interval<Integer>> list = tree.query(-74038);
		assertEquals(0, list.size());
	}
}
