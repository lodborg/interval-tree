package com.lodborg.intervaltree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RandomizedTest {
	private long listTime, treeTime;
	private long treeCreationTime;

	@Ignore @Test
	public void randomized(){
		for (int i=0; i<1; i++){
			randomizedSingleRun();
		}
		System.out.println("List time: "+listTime);
		System.out.println("Tree time: "+treeTime);
		System.out.println("Tree creation time: "+treeCreationTime);
	}

	private void randomizedSingleRun(){
		List<Interval<Integer>> list = new ArrayList<>();
		IntervalTree<Integer> tree = new IntervalTree<>();
		int amount = 100_000;
		int range = 100000;
		int lengthRange = 100;
		int checks = 10000;

		for (int i=0; i<amount; i++){
			int kind = getRandomInRange(0, 7);
			IntegerInterval next;
			int begin, length;
			begin = getRandomInRange(-range, range);
			length = getRandomInRange(0, lengthRange);
			next = new IntegerInterval(begin, begin+length, Interval.Bounded.CLOSED);
			/*switch (kind){
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
					next = new IntegerInterval(begin, Unbounded.CLOSED_LEFT);
					break;
				case 5:
					begin = getRandomInRange(-range, range);
					next = new IntegerInterval(begin, Unbounded.OPEN_LEFT);
					break;
				case 6:
					begin = getRandomInRange(-range, range);
					next = new IntegerInterval(begin, Unbounded.CLOSED_RIGHT);
					break;
				default:
					begin = getRandomInRange(-range, range);
					next = new IntegerInterval(begin, Unbounded.OPEN_RIGHT);
					break;
			}*/

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
		System.out.print("tree.addInterval(new IntegerInterval().create("+(inter.getStart() == null ? "null" : inter.getStart()));
		System.out.println(", "+inter.isStartInclusive()+", "+(inter.getEnd() == null ? "null" : inter.getEnd())+", "+inter.isEndInclusive()+"));");
	}

	private boolean check(IntervalTree<Integer> tree, List<Interval<Integer>> list, Integer point) {
		long time = System.currentTimeMillis();
		List<Interval<Integer>> fromList = linearCheck(list, point);
		listTime += System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		List<Interval<Integer>> fromTree = new ArrayList<>(tree.query(point));
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
