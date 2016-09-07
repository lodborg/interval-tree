package com.lodborg.intervaltree;

import org.junit.Ignore;
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
			tree.addInterval(interval);
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
		tree.addInterval(new IntegerInterval(1, 3, Bounded.CLOSED));
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
			tree.addInterval(next);
		Set<Interval<Integer>> result = new HashSet<>();
		for (Interval<Integer> next: tree)
			result.add(next);
		assertThat(set, is(result));
	}

	@Ignore @Test
	public void test_iteratorRemove(){
		IntervalTree<Integer> tree = new IntervalTree<>();
		Interval<Integer> target = new IntegerInterval(12, 22, Bounded.CLOSED_RIGHT);
		tree.addInterval(new IntegerInterval(2, 10, Bounded.CLOSED_LEFT));
		tree.addInterval(target);
		tree.addInterval(new IntegerInterval(1, 5, Bounded.OPEN));
		Iterator<Interval<Integer>> it = tree.root.iterator();
		while (it.hasNext()){
			Interval<Integer> next = it.next();
			if (next == target)
				it.remove();
		}
		it = tree.root.iterator();
		while (it.hasNext())
			assertNotEquals(it.next(), target);
	}
}
