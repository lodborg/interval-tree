package com.lodborg.intervaltree;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.lodborg.intervaltree.Interval.*;

import java.util.*;

public class IntervalTreeTest {
	long listTime, treeTime;
	long treeCreationTime;

	@Test
	public void firstTest() {
		IntervalTree<Integer> tree = new IntervalTree<>();

		tree.addInterval(new IntegerInterval(10, 20, Bounded.CLOSED));
		tree.addInterval(new IntegerInterval(20, 40, Bounded.CLOSED_LEFT));
		tree.addInterval(new IntegerInterval(15, 35, Bounded.CLOSED));
		tree.addInterval(new IntegerInterval(-20, 15, Bounded.OPEN));
		tree.addInterval(new IntegerInterval(0, 16, Bounded.OPEN));
		tree.addInterval(new IntegerInterval(32, Unbounded.LEFT_CLOSED));
		tree.addInterval(new IntegerInterval(17, Unbounded.RIGHT_CLOSED));
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
		tree.addInterval(new IntegerInterval(7450, Unbounded.LEFT_OPEN));
		tree.addInterval(new IntegerInterval(209, Unbounded.RIGHT_OPEN));
		tree.addInterval(new IntegerInterval(2774, Unbounded.RIGHT_CLOSED));

		assertEquals(1, tree.query(8659).size());

		tree = new IntervalTree<>();
		tree.addInterval(new IntegerInterval(6213, Unbounded.RIGHT_OPEN));
		tree.addInterval(new IntegerInterval(684, Unbounded.LEFT_CLOSED));
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

	@Test
	public void randomized(){
		for (int i=0; i<1; i++){
			randomizedSingleRun();
		}
		System.out.println("List time: "+listTime);
		System.out.println("Tree time: "+treeTime);
		System.out.println("Tree creation time: "+treeCreationTime);
	}

	public void randomizedSingleRun(){
		List<Interval<Integer>> list = new ArrayList<>();
		IntervalTree<Integer> tree = new IntervalTree<>();
		int amount = 3000000;
		int range = 100000;
		int lengthRange = 100;
		int checks = 100;

		for (int i=0; i<amount; i++){
			int kind = getRandomInRange(0, 7);
			IntegerInterval next;
			int begin, length;
			/*begin = getRandomInRange(-range, range);
			length = getRandomInRange(0, lengthRange);
			next = new IntegerInterval(begin, begin+length, Bounded.CLOSED);*/
			switch (kind){
				case 0:
					begin = getRandomInRange(-range, range);
					length = getRandomInRange(0, lengthRange);
					next = new IntegerInterval(begin, begin+length, Bounded.CLOSED);
					break;
				case 1:
					begin = getRandomInRange(-range, range);
					length = getRandomInRange(0, lengthRange);
					next = new IntegerInterval(begin, begin+length, Bounded.OPEN);
					break;
				case 2:
					begin = getRandomInRange(-range, range);
					length = getRandomInRange(0, lengthRange);
					next = new IntegerInterval(begin, begin+length, Bounded.CLOSED_LEFT);
					break;
				case 3:
					begin = getRandomInRange(-range, range);
					length = getRandomInRange(0, lengthRange);
					next = new IntegerInterval(begin, begin+length, Bounded.CLOSED_RIGHT);
					break;
				case 4:
					begin = getRandomInRange(-range, range);
					next = new IntegerInterval(begin, Unbounded.LEFT_CLOSED);
					break;
				case 5:
					begin = getRandomInRange(-range, range);
					next = new IntegerInterval(begin, Unbounded.LEFT_OPEN);
					break;
				case 6:
					begin = getRandomInRange(-range, range);
					next = new IntegerInterval(begin, Unbounded.RIGHT_CLOSED);
					break;
				default:
					begin = getRandomInRange(-range, range);
					next = new IntegerInterval(begin, Unbounded.RIGHT_OPEN);
					break;
			}

			list.add(next);
			long time = System.currentTimeMillis();
			tree.addInterval(next);
			treeCreationTime += System.currentTimeMillis() - time;
		}

		for (int i=0; i<checks; i++){
			Integer point = getRandomInRange(-range, range);
			boolean check = check(tree, list, point);
			if (!check){
				System.out.println("Point: "+point);
				for (Interval<Integer> interval: list){
					print(interval);
				}
			}
			assertEquals(true, check);
		}
	}

	private void print(Interval<Integer> inter){
		System.out.print("tree.addInterval(new IntegerInterval().create("+(inter.start == null ? "null" : inter.start));
		System.out.println(", "+inter.isStartInclusive+", "+(inter.end == null ? "null" : inter.end)+", "+inter.isEndInclusive+"));");
	}

	private boolean check(IntervalTree<Integer> tree, List<Interval<Integer>> list, Integer point) {
		long time = System.currentTimeMillis();
		List<Interval<Integer>> fromList = linearCheck(list, point);
		listTime += System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		List<Interval<Integer>> fromTree = tree.query(point);
		treeTime += System.currentTimeMillis() - time;
		//return true;
		Collections.sort(fromList, Interval.startComparator);
		Collections.sort(fromTree, Interval.startComparator);
		int treeIndex = 0;
		for (int listIndex = 0; listIndex < fromList.size(); listIndex++){
			if (listIndex > 0 && fromList.get(listIndex).equals(fromList.get(listIndex-1)))
				continue;
			if (treeIndex > fromTree.size() || !fromList.get(listIndex).equals(fromTree.get(treeIndex)))
				return false;
			treeIndex++;
		}
		return treeIndex == fromTree.size();
	}

	private List<Interval<Integer>> linearCheck(List<Interval<Integer>> list, Integer point) {
		List<Interval<Integer>> res = new ArrayList<>();
		for (Interval<Integer> interval: list){
			if (interval.contains(point))
				res.add(interval);
		}
		return res;
	}

	private int getRandomInRange(int min, int max){
		return min + (int)(Math.random()*(Math.abs(min)+Math.abs(max)+1));
	}
}
