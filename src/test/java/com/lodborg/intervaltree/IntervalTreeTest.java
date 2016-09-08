package com.lodborg.intervaltree;

import org.junit.Test;

import com.lodborg.intervaltree.Interval.*;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class IntervalTreeTest {

	@Test
	public void test_deleteNodeAfterAssimilation(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer> a = new IntegerInterval(0, 100, Bounded.CLOSED);
		Interval<Integer> b = new IntegerInterval(30, 40, Bounded.CLOSED_LEFT);
		Interval<Integer> c = new IntegerInterval(10, 20, Bounded.CLOSED_RIGHT);
		tree.add(a);
		tree.add(b);
		tree.add(c);
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
		tree.add(a);
		tree.add(b);
		tree.add(c);
		tree.remove(a);
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
		tree.add(a);
		tree.add(b);
		tree.remove(a);
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
		tree.add(a);
		tree.add(b);
		tree.add(c);
		tree.add(d);
		tree.remove(a);

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
		tree.add(a);
		tree.add(b);
		tree.add(c);
		tree.add(d);
		tree.add(e);
		tree.add(f);
		tree.add(g);
		tree.remove(a);

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
		tree.add(a);
		tree.add(b);
		tree.add(c);
		tree.add(d);
		tree.add(e);
		tree.add(f);
		tree.add(g);

		TreeNode<Integer> nodeF = tree.root.left.right;
		TreeNode<Integer> nodeG = tree.root.left.right.right;
		TreeNode<Integer> nodeE = tree.root.left.left;
		TreeNode<Integer> nodeC = tree.root.right;

		tree.remove(a);

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
		tree.add(a);
		assertNull(tree.root);
	}

	@Test
	public void test_removeFromEmptyTree(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		tree.remove(new IntegerInterval(10, 20, Bounded.CLOSED));
		assertNull(tree.root);
	}

	@Test
	public void test_removeEmptyInterval(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval a = new IntegerInterval(1, 4, Bounded.OPEN);
		tree.add(a);
		tree.remove(new IntegerInterval(30, 20, Bounded.CLOSED));
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
		tree.add(a);
		tree.add(b);
		tree.add(c);
		tree.remove(new IntegerInterval(10, 20, Bounded.CLOSED));
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
		tree.add(a);
		tree.remove(new IntegerInterval(10, 40, Bounded.OPEN));
		assertTrue(tree.root.decreasing.contains(a));
		assertTrue(tree.root.increasing.contains(a));
		assertEquals(1, tree.root.increasing.size());
		assertEquals(1, tree.root.decreasing.size());
	}

	@Test
	public void test_queryIntervalNormal(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval a = new IntegerInterval(22, Unbounded.CLOSED_RIGHT);
		IntegerInterval b = new IntegerInterval(7, 13, Bounded.CLOSED);
		IntegerInterval c = new IntegerInterval(21, 24, Bounded.OPEN);
		IntegerInterval d = new IntegerInterval(32, Unbounded.CLOSED_LEFT);

		tree.add(a);
		tree.add(b);
		tree.add(c);
		tree.add(d);
		IntegerInterval queryInterval = new IntegerInterval(18, 29, Bounded.CLOSED);
		Set<Interval<Integer>> res = tree.query(queryInterval);

		assertEquals(2, res.size());
		assertTrue(res.contains(a));
		assertTrue(res.contains(c));
		assertFalse(res.contains(b));
		assertFalse(res.contains(d));
	}

	@Test
	public void test_queryIntervalOpenAndClosedEndpoints(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval aa = new IntegerInterval(18, Unbounded.CLOSED_RIGHT);
		IntegerInterval ab = new IntegerInterval(18, Unbounded.CLOSED_LEFT);
		IntegerInterval ac = new IntegerInterval(18, Unbounded.OPEN_LEFT);
		IntegerInterval ad = new IntegerInterval(18, Unbounded.OPEN_RIGHT);

		IntegerInterval ba = new IntegerInterval(29, Unbounded.CLOSED_RIGHT);
		IntegerInterval bb = new IntegerInterval(29, Unbounded.CLOSED_LEFT);
		IntegerInterval bc = new IntegerInterval(29, Unbounded.OPEN_LEFT);
		IntegerInterval bd = new IntegerInterval(29, Unbounded.OPEN_RIGHT);

		IntegerInterval c = new IntegerInterval(7, 13, Bounded.CLOSED);
		IntegerInterval d = new IntegerInterval(21, 24, Bounded.OPEN);
		IntegerInterval e = new IntegerInterval(32, 45, Bounded.CLOSED_RIGHT);

		IntegerInterval queryInterval = new IntegerInterval(18, 29, Bounded.CLOSED_RIGHT);

		IntegerInterval[] arr = new IntegerInterval[]{
				aa, ab, ac, ad, ba, bb, bc, bd, c, d, e
		};

		for (IntegerInterval interval: arr)
			tree.add(interval);

		Set<Interval<Integer>> res = tree.query(queryInterval);
		List<IntegerInterval> expected = Arrays.asList(ab, ac, ba, bb, bd, d);
		assertTrue(res.containsAll(expected));
		assertEquals(6, res.size());
	}

	@Test
	public void test_queryIntervalReturnsASuperInterval(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval a = new IntegerInterval(8, 20, Bounded.CLOSED_LEFT);
		IntegerInterval query = new IntegerInterval(0, 100, Bounded.OPEN);
		tree.add(a);
		Set<Interval<Integer>> set = tree.query(query);
		assertEquals(1, set.size());
		assertTrue(set.contains(a));
	}

	@Test
	public void test_queryIntervalChangesInTheTreeDontAffectReturnedSet(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval aa = new IntegerInterval(18, Unbounded.CLOSED_RIGHT);
		IntegerInterval ab = new IntegerInterval(18, Unbounded.CLOSED_LEFT);
		IntegerInterval ac = new IntegerInterval(18, Unbounded.OPEN_LEFT);
		IntegerInterval ad = new IntegerInterval(18, Unbounded.OPEN_RIGHT);

		IntegerInterval ba = new IntegerInterval(29, Unbounded.CLOSED_RIGHT);
		IntegerInterval bb = new IntegerInterval(29, Unbounded.CLOSED_LEFT);
		IntegerInterval bc = new IntegerInterval(29, Unbounded.OPEN_LEFT);
		IntegerInterval bd = new IntegerInterval(29, Unbounded.OPEN_RIGHT);

		IntegerInterval c = new IntegerInterval(7, 13, Bounded.CLOSED);
		IntegerInterval d = new IntegerInterval(21, 24, Bounded.OPEN);
		IntegerInterval e = new IntegerInterval(32, 45, Bounded.CLOSED_RIGHT);

		IntegerInterval queryInterval = new IntegerInterval(18, 29, Bounded.CLOSED_RIGHT);
		IntegerInterval[] arr = new IntegerInterval[]{
				aa, ab, ac, ad, ba, bb, bc, bd, c, d, e
		};

		for (IntegerInterval interval: arr)
			tree.add(interval);
		Set<Interval<Integer>> res = tree.query(queryInterval);
		for (IntegerInterval interval: arr)
			tree.remove(interval);

		List<IntegerInterval> expected = Arrays.asList(ab, ac, ba, bb, bd, d);
		assertTrue(res.containsAll(expected));
		assertEquals(6, res.size());
	}

	@Test
	public void test_queryIntervalWithNewEndPoints(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval a = new IntegerInterval(1, 5, Bounded.CLOSED);
		IntegerInterval b = new IntegerInterval(7, 20, Bounded.OPEN);
		IntegerInterval c = new IntegerInterval(5, 18, Bounded.CLOSED_LEFT);

		tree.add(a);
		tree.add(b);
		tree.add(c);
		IntegerInterval queryInterval = new IntegerInterval(3, 6, Bounded.CLOSED);
		Set<Interval<Integer>> set = tree.query(queryInterval);

		assertEquals(2, set.size());
		assertTrue(set.contains(a));
		assertTrue(set.contains(c));
	}

	@Test
	public void test_queryIntervalOffByOne(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval a = new IntegerInterval(1, 5, Bounded.OPEN);
		tree.add(a);
		assertEquals(0, tree.query(new IntegerInterval(4, 20, Bounded.OPEN)).size());
		assertEquals(1, tree.query(new IntegerInterval(4, 20, Bounded.CLOSED)).size());
		assertEquals(1, tree.query(new IntegerInterval(4, 20, Bounded.CLOSED_LEFT)).size());
		assertEquals(0, tree.query(new IntegerInterval(4, 20, Bounded.CLOSED_RIGHT)).size());

		tree = new IntervalTree<>();
		a = new IntegerInterval(1, 5, Bounded.CLOSED);
		tree.add(a);
		assertEquals(1, tree.query(new IntegerInterval(4, 20, Bounded.OPEN)).size());
		assertEquals(1, tree.query(new IntegerInterval(4, 20, Bounded.CLOSED)).size());
		assertEquals(1, tree.query(new IntegerInterval(4, 20, Bounded.CLOSED_LEFT)).size());
		assertEquals(1, tree.query(new IntegerInterval(4, 20, Bounded.CLOSED_RIGHT)).size());

		tree = new IntervalTree<>();
		a = new IntegerInterval(1, 5, Bounded.OPEN);
		tree.add(a);
		assertEquals(0, tree.query(new IntegerInterval(-5, 2, Bounded.OPEN)).size());
		assertEquals(1, tree.query(new IntegerInterval(-5, 2, Bounded.CLOSED)).size());
		assertEquals(0, tree.query(new IntegerInterval(-5, 2, Bounded.CLOSED_LEFT)).size());
		assertEquals(1, tree.query(new IntegerInterval(-5, 2, Bounded.CLOSED_RIGHT)).size());

		tree = new IntervalTree<>();
		a = new IntegerInterval(1, 5, Bounded.CLOSED);
		tree.add(a);
		assertEquals(1, tree.query(new IntegerInterval(-5, 2, Bounded.OPEN)).size());
		assertEquals(1, tree.query(new IntegerInterval(-5, 2, Bounded.CLOSED)).size());
		assertEquals(1, tree.query(new IntegerInterval(-5, 2, Bounded.CLOSED_LEFT)).size());
		assertEquals(1, tree.query(new IntegerInterval(-5, 2, Bounded.CLOSED_RIGHT)).size());
	}

	@Test
	public void test_queryIntervalOppositeEndpointsEqual(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval a = new IntegerInterval(1, 5, Bounded.OPEN);
		tree.add(a);

		assertEquals(0, tree.query(new IntegerInterval(5, 10, Bounded.OPEN)).size());
		assertEquals(0, tree.query(new IntegerInterval(5, 10, Bounded.CLOSED)).size());
		assertEquals(0, tree.query(new IntegerInterval(5, 10, Bounded.CLOSED_LEFT)).size());
		assertEquals(0, tree.query(new IntegerInterval(5, 10, Bounded.CLOSED_RIGHT)).size());

		tree = new IntervalTree<>();
		a = new IntegerInterval(1, 5, Bounded.CLOSED);
		tree.add(a);

		assertEquals(0, tree.query(new IntegerInterval(5, 10, Bounded.OPEN)).size());
		assertEquals(1, tree.query(new IntegerInterval(5, 10, Bounded.CLOSED)).size());
		assertEquals(1, tree.query(new IntegerInterval(5, 10, Bounded.CLOSED_LEFT)).size());
		assertEquals(0, tree.query(new IntegerInterval(5, 10, Bounded.CLOSED_RIGHT)).size());

		tree = new IntervalTree<>();
		a = new IntegerInterval(1, 5, Bounded.OPEN);
		tree.add(a);

		assertEquals(0, tree.query(new IntegerInterval(-8, 1, Bounded.OPEN)).size());
		assertEquals(0, tree.query(new IntegerInterval(-8, 1, Bounded.CLOSED)).size());
		assertEquals(0, tree.query(new IntegerInterval(-8, 1, Bounded.CLOSED_LEFT)).size());
		assertEquals(0, tree.query(new IntegerInterval(-8, 1, Bounded.CLOSED_RIGHT)).size());

		tree = new IntervalTree<>();
		a = new IntegerInterval(1, 5, Bounded.CLOSED);
		tree.add(a);

		assertEquals(0, tree.query(new IntegerInterval(-8, 1, Bounded.OPEN)).size());
		assertEquals(1, tree.query(new IntegerInterval(-8, 1, Bounded.CLOSED)).size());
		assertEquals(0, tree.query(new IntegerInterval(-8, 1, Bounded.CLOSED_LEFT)).size());
		assertEquals(1, tree.query(new IntegerInterval(-8, 1, Bounded.CLOSED_RIGHT)).size());
	}

	@Test
	public void test_rangeQueryToRightIntersectsMoreThanTwoMidpoints(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer>[] arr = new IntegerInterval[]{
				new IntegerInterval(6, 10, Bounded.OPEN),
				new IntegerInterval(-5, 0, Bounded.CLOSED),
				new IntegerInterval(40, 100, Bounded.CLOSED),
				new IntegerInterval(190, 300, Bounded.CLOSED)
		};
		for (Interval<Integer> interval: arr)
			tree.add(interval);
		Set<Interval<Integer>> expected = new HashSet<>(Arrays.asList(arr[0], arr[2]));
		Interval<Integer> query = new IntegerInterval(7, 170, Bounded.CLOSED_LEFT);
		assertThat(new HashSet<>(tree.query(query)), is(new HashSet<>(expected)));
	}

	/**
	 * Left branch of a range query, root of subtree contains no valid results,
	 * right grandchild has some though.
	 */
	@Test
	public void test_rangeQueryLeftWithOnlyRightGrandchildContainingValidResults(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer>[] arr = new IntegerInterval[]{
				new IntegerInterval(6, 10, Bounded.OPEN),
				new IntegerInterval(-5, 0, Bounded.CLOSED),
				new IntegerInterval(40, 100, Bounded.CLOSED),
				new IntegerInterval(1, 4, Bounded.CLOSED)
		};
		for (Interval<Integer> interval: arr)
			tree.add(interval);
		Set<Interval<Integer>> expected = new HashSet<>(Arrays.asList(arr[0], arr[3]));
		Interval<Integer> query = new IntegerInterval(3, 8, Bounded.CLOSED);
		assertThat(new HashSet<>(tree.query(query)), is(new HashSet<>(expected)));
	}

	/**
	 * Left branch of a range query, root of subtree contains valid results and has
	 * a right child.
	 */
	@Test
	public void test_rangeQueryLeftWithRootOfSubtreeAndRightGrandchildContainingResults(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer>[] arr = new IntegerInterval[]{
				new IntegerInterval(6, 10, Bounded.OPEN),
				new IntegerInterval(12, 30, Bounded.CLOSED),
				new IntegerInterval(-100, -50, Bounded.CLOSED),
				new IntegerInterval(-1, 4, Bounded.CLOSED)
		};
		for (Interval<Integer> interval: arr)
			tree.add(interval);
		Set<Interval<Integer>> expected = new HashSet<>(Arrays.asList(arr[0], arr[2], arr[3]));
		Interval<Integer> query = new IntegerInterval(-80, 9, Bounded.CLOSED_LEFT);
		assertThat(new HashSet<>(tree.query(query)), is(new HashSet<>(expected)));
	}

	/**
	 * Left branch of a range query, root of subtree contains valid results and has
	 * a right child. The middlepoint of the root of left tree is not within the query.
	 */
	@Test
	public void test_rangeQueryLeftSubtreeMidpointNotInQuery(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer>[] arr = new IntegerInterval[]{
				new IntegerInterval(6, 10, Bounded.OPEN),
				new IntegerInterval(12, 30, Bounded.CLOSED),
				new IntegerInterval(-100, -50, Bounded.CLOSED),
				new IntegerInterval(-1, 4, Bounded.CLOSED)
		};
		for (Interval<Integer> interval: arr)
			tree.add(interval);
		Set<Interval<Integer>> expected = new HashSet<>(Arrays.asList(arr[0], arr[2], arr[3]));
		Interval<Integer> query = new IntegerInterval(-70, 9, Bounded.CLOSED_LEFT);
		assertThat(new HashSet<>(tree.query(query)), is(new HashSet<>(expected)));
	}

	/**
	 * Right branch of a range query, root of subtree contains no valid results,
	 * left grandchild has some though.
	 */
	@Test
	public void test_rangeQueryRightWithOnlyLeftGrandchildContainingValidResults(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer>[] arr = new IntegerInterval[]{
				new IntegerInterval(6, 10, Bounded.OPEN),
				new IntegerInterval(-5, 0, Bounded.CLOSED),
				new IntegerInterval(40, 100, Bounded.CLOSED),
				new IntegerInterval(20, 30, Bounded.CLOSED)
		};
		for (Interval<Integer> interval: arr)
			tree.add(interval);
		Set<Interval<Integer>> expected = new HashSet<>(Arrays.asList(arr[0], arr[3]));
		Interval<Integer> query = new IntegerInterval(7, 22, Bounded.CLOSED_LEFT);
		assertThat(new HashSet<>(tree.query(query)), is(new HashSet<>(expected)));
	}

	/**
	 * Right branch of a range query, root of subtree contains valid results and has
	 * a left child.
	 */
	@Test
	public void test_rangeQueryRightWithRootOfSubtreeAndLeftGrandchildContainingResults(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer>[] arr = new IntegerInterval[]{
				new IntegerInterval(6, 10, Bounded.OPEN),
				new IntegerInterval(-5, 0, Bounded.CLOSED),
				new IntegerInterval(40, 100, Bounded.CLOSED),
				new IntegerInterval(20, 30, Bounded.CLOSED)
		};
		for (Interval<Integer> interval: arr)
			tree.add(interval);
		Set<Interval<Integer>> expected = new HashSet<>(Arrays.asList(arr[0], arr[2], arr[3]));
		Interval<Integer> query = new IntegerInterval(7, 75, Bounded.CLOSED_LEFT);
		assertThat(new HashSet<>(tree.query(query)), is(new HashSet<>(expected)));
	}

	@Test
	public void test_rangeQueryEmptyResult(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		assertTrue(tree.query(new IntegerInterval()).isEmpty());
		tree.add(new IntegerInterval(6, 10, Bounded.OPEN));
		tree.add(new IntegerInterval(-5, 0, Bounded.CLOSED));
		tree.add(new IntegerInterval(40, 100, Bounded.CLOSED));
		tree.add(new IntegerInterval(20, 30, Bounded.CLOSED));
		assertTrue(tree.query(new IntegerInterval(35, 39, Bounded.CLOSED)).isEmpty());
		assertTrue(tree.query(new IntegerInterval(80, 25, Bounded.CLOSED)).isEmpty());
	}

	@Test
	public void test_sizeAfterAddingNonExistingIntervals(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval[] arr = new IntegerInterval[]{
				new IntegerInterval(5, 25, Bounded.CLOSED),
				new IntegerInterval(30, 50, Bounded.CLOSED_LEFT),
				new IntegerInterval(4, 20, Bounded.CLOSED_RIGHT),
				new IntegerInterval(-1, 8, Bounded.OPEN),
				new IntegerInterval(-2, 8, Bounded.OPEN),
				new IntegerInterval(22, 60, Bounded.OPEN),
				new IntegerInterval(100, 200, Bounded.CLOSED),
				new IntegerInterval(300, 400, Bounded.CLOSED_LEFT)
		};
		int size = 0;
		assertEquals(size, tree.size());
		for (IntegerInterval next: arr){
			size++;
			tree.add(next);
			assertEquals(size, tree.size());
		}
	}

	@Test
	public void test_sizeAfterAddingExistingIntervals(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval[] arr = new IntegerInterval[]{
				new IntegerInterval(5, 25, Bounded.CLOSED),
				new IntegerInterval(30, 50, Bounded.CLOSED_LEFT),
				new IntegerInterval(4, 20, Bounded.CLOSED_RIGHT),
				new IntegerInterval(-1, 8, Bounded.OPEN),
				new IntegerInterval(-2, 8, Bounded.OPEN),
				new IntegerInterval(22, 60, Bounded.OPEN),
				new IntegerInterval(100, 200, Bounded.CLOSED),
				new IntegerInterval(300, 400, Bounded.CLOSED_LEFT)
		};
		for (IntegerInterval next: arr){
			tree.add(next);
		}
		int size = tree.size();
		assertEquals(size, arr.length);
		for (IntegerInterval next: arr){
			tree.add(next);
			assertEquals(size, tree.size());
		}
	}

	@Test
	public void test_sizeAfterRemovingNonExistingIntervals(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval[] arr = new IntegerInterval[]{
				new IntegerInterval(5, 25, Bounded.CLOSED),
				new IntegerInterval(30, 50, Bounded.CLOSED_LEFT),
				new IntegerInterval(4, 20, Bounded.CLOSED_RIGHT),
				new IntegerInterval(-1, 8, Bounded.OPEN),
				new IntegerInterval(-2, 8, Bounded.OPEN),
				new IntegerInterval(22, 60, Bounded.OPEN),
				new IntegerInterval(100, 200, Bounded.CLOSED),
				new IntegerInterval(300, 400, Bounded.CLOSED_LEFT)
		};
		IntegerInterval[] toRemove = new IntegerInterval[]{
				new IntegerInterval(9, 20, Bounded.CLOSED_LEFT),
				new IntegerInterval(500, 500, Bounded.CLOSED),
				new IntegerInterval(29, 49, Bounded.OPEN),
				new IntegerInterval(100, Unbounded.CLOSED_LEFT)
		};
		for (IntegerInterval next: arr){
			tree.add(next);
		}
		int size = tree.size();
		assertEquals(arr.length, tree.size());

		for (IntegerInterval next: toRemove){
			tree.remove(next);
			assertEquals(size, tree.size());
		}
	}

	@Test
	public void test_sizeAfterRemovingExistingIntervals(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval[] arr = new IntegerInterval[]{
				new IntegerInterval(5, 25, Bounded.CLOSED),
				new IntegerInterval(30, 50, Bounded.CLOSED_LEFT),
				new IntegerInterval(4, 20, Bounded.CLOSED_RIGHT),
				new IntegerInterval(-1, 8, Bounded.OPEN),
				new IntegerInterval(-2, 8, Bounded.OPEN),
				new IntegerInterval(22, 60, Bounded.OPEN),
				new IntegerInterval(100, 200, Bounded.CLOSED),
				new IntegerInterval(300, 400, Bounded.CLOSED_LEFT)
		};
		for (IntegerInterval next: arr){
			tree.add(next);
		}
		int size = tree.size();
		assertEquals(arr.length, tree.size());

		for (IntegerInterval next: arr){
			size--;
			tree.remove(next);
			assertEquals(size, tree.size());
		}
	}

	@Test
	public void test_clearOnEmptyTree(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		tree.clear();
		assertTrue(tree.isEmpty());
		assertEquals(0, tree.size());
		tree.add(new IntegerInterval());
		assertEquals(1, tree.size());
	}

	@Test
	public void test_clearOnNonEmptyTree(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		tree.add(new IntegerInterval());
		tree.add(new IntegerInterval(1, 5, Bounded.CLOSED_LEFT));
		tree.add(new IntegerInterval(228, Unbounded.OPEN_RIGHT));
		assertEquals(3, tree.size());
		assertFalse(tree.isEmpty());
		tree.clear();
		assertTrue(tree.isEmpty());
		assertEquals(0, tree.size());
	}

	@Test
	public void test_contains(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		IntegerInterval[] arr = new IntegerInterval[]{
				new IntegerInterval(5, 22, Bounded.CLOSED_LEFT),
				new IntegerInterval(23, 90, Bounded.OPEN),
				new IntegerInterval(0, 4, Bounded.CLOSED),
				new IntegerInterval(-20, -10, Bounded.CLOSED_RIGHT),
				new IntegerInterval(30, 80, Bounded.CLOSED),
				new IntegerInterval(8, 33, Bounded.OPEN),
				new IntegerInterval(-5, Unbounded.CLOSED_RIGHT),
				new IntegerInterval(10, Unbounded.CLOSED_LEFT),
				new IntegerInterval(12, Unbounded.OPEN_LEFT),
				new IntegerInterval(0, Unbounded.OPEN_RIGHT)
		};

		IntegerInterval[] notContained = new IntegerInterval[]{
				new IntegerInterval(5, 22, Bounded.CLOSED_RIGHT),
				new IntegerInterval(-5, Unbounded.CLOSED_LEFT),
				new IntegerInterval(-5, Unbounded.OPEN_RIGHT),
				new IntegerInterval(31, 80, Bounded.CLOSED),
				new IntegerInterval(-19, -11, Bounded.OPEN),
				new IntegerInterval(-100, -50, Bounded.CLOSED)
		};

		for (IntegerInterval next: arr)
			tree.add(next);
		for (IntegerInterval next: arr)
			assertTrue(tree.contains(next));
		for (IntegerInterval next: notContained)
			assertFalse(tree.contains(next));
	}

	@Test
	public void test_containsBroken(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		assertFalse(tree.contains(null));
		assertFalse(tree.contains(new IntegerInterval(1, Unbounded.CLOSED_LEFT)));
		assertFalse(tree.contains(new IntegerInterval(100, 0, Bounded.CLOSED)));
		assertFalse(tree.contains(9));

		tree.add(new IntegerInterval());
		
		assertFalse(tree.contains(new IntegerInterval(1, Unbounded.CLOSED_LEFT)));
		assertFalse(tree.contains(null));
		assertFalse(tree.contains(new IntegerInterval(100, 0, Bounded.CLOSED)));
		assertFalse(tree.contains(9));
		try {
			tree.contains(new DateInterval(new Date(), Unbounded.CLOSED_LEFT));
			fail();
		} catch (Exception e){
			assertTrue(e instanceof ClassCastException);
		}
	}

	@Test
	public void firstTest() {
		IntervalTree<Integer> tree = new IntervalTree<>();

		tree.add(new IntegerInterval(10, 20, Bounded.CLOSED));
		tree.add(new IntegerInterval(20, 40, Bounded.CLOSED_LEFT));
		tree.add(new IntegerInterval(15, 35, Bounded.CLOSED));
		tree.add(new IntegerInterval(-20, 15, Bounded.OPEN));
		tree.add(new IntegerInterval(0, 16, Bounded.OPEN));
		tree.add(new IntegerInterval(32, Unbounded.CLOSED_LEFT));
		tree.add(new IntegerInterval(17, Unbounded.CLOSED_RIGHT));
		tree.add(new IntegerInterval());

		assertEquals(5, tree.query(15).size());
		assertEquals(2, tree.query(-20).size());
		assertEquals(3, tree.query(-17).size());
		assertEquals(5, tree.query(11).size());
		assertEquals(3, tree.query(-8).size());

		tree = new IntervalTree<>();
		tree.add(new IntegerInterval(10, 20, Bounded.OPEN));
		tree.add(new IntegerInterval(10, 12, Bounded.OPEN));
		tree.add(new IntegerInterval(-1000, 8, Bounded.CLOSED));

		assertEquals(2, tree.query(11).size());
		assertEquals(0, tree.query(9).size());
		assertEquals(1, tree.query(0).size());

		tree = new IntervalTree<>();
		tree.add(new IntegerInterval(7450, Unbounded.OPEN_LEFT));
		tree.add(new IntegerInterval(209, Unbounded.OPEN_RIGHT));
		tree.add(new IntegerInterval(2774, Unbounded.CLOSED_RIGHT));

		assertEquals(1, tree.query(8659).size());

		tree = new IntervalTree<>();
		tree.add(new IntegerInterval(6213, Unbounded.OPEN_RIGHT));
		tree.add(new IntegerInterval(684, Unbounded.CLOSED_LEFT));
		tree.add(new IntegerInterval(-4657, -4612, Bounded.OPEN));

		assertEquals(1, tree.query(359).size());

		tree = new IntervalTree<>();
		tree.add(new IntegerInterval().create(8705, true, 9158, true));
		tree.add(new IntegerInterval().create(8899, false, 8966, false));
		tree.add(new IntegerInterval().create(-7200, true, null, true));
		tree.add(new IntegerInterval().create(315, false, 408, true));
		tree.add(new IntegerInterval().create(965, false, 1218, true));

		assertEquals(2, tree.query(9042).size());

		tree = new IntervalTree<>();
		tree.add(new IntegerInterval().create(2988, true, 3362, false));
		assertEquals(1, tree.query(2988).size());

		tree = new IntervalTree<>();
		tree.add(new IntegerInterval().create(8457, true, 8926, true));
		tree.add(new IntegerInterval().create(2988, true, 3362, false));
		tree.add(new IntegerInterval().create(null, true, -523, true));
		tree.add(new IntegerInterval().create(-5398, false, -5250, true));
		tree.add(new IntegerInterval().create(-2912, false, -2727, true));
		assertEquals(1, tree.query(2988).size());

		tree = new IntervalTree<>();
		tree.add(new IntegerInterval().create(91449, true, 91468, true));
		tree.add(new IntegerInterval().create(-74038, false, -74037, true));
		tree.add(new IntegerInterval().create(-53053, false, null, true));
		assertEquals(0, tree.query(-74038).size());
	}
}
