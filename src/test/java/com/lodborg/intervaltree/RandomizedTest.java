package com.lodborg.intervaltree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.lodborg.intervaltree.Interval.*;

import static org.junit.Assert.assertEquals;

public class RandomizedTest {

	@Ignore
	@Test
	public void randomized_interval(){
		Configuration config = new Configuration();
		config.amount = 5_000_0;
		config.range = 100_000_000;
		config.lengthRange = 200000;
		config.checks = 2000;

		Checker checker = new Checker<Interval<Integer>>(config) {
			public Set<Interval<Integer>> treeCheck(IntervalTree<Integer> tree, Interval<Integer> query) {
				return tree.query(query);
			}

			public boolean listCondition(Interval<Integer> listInterval, Interval<Integer> query) {
				return listInterval.intersects(query);
			}

			public Interval<Integer> createRandom(){
				return randomIntervalClosed();
			}
		};

		for (int i=0; i<1; i++){
			checker.randomizedSingleRun();
		}
		checker.printResults();
	}

	private class Configuration {
		int amount, range, lengthRange, checks;
	}

	private class Output {
		long listCreationTime, listSearchTime, treeCreationTime, treeSearchTime;
	}

	@Ignore
	@Test
	public void randomized_point(){
		Configuration config =  new Configuration();
		config.amount = 500_000;
		config.range = 100_000_000;
		config.lengthRange = 200;
		config.checks = 2000;

		Checker<Integer> checker = new Checker<Integer>(config) {
			public Set<Interval<Integer>> treeCheck(IntervalTree<Integer> tree, Integer query) {
				return tree.query(query);
			}

			public boolean listCondition(Interval<Integer> listInterval, Integer query) {
				return listInterval.contains(query);
			}

			Integer createRandom() {
				return getRandomInRange(-config.range, config.range);
			}
		};

		for (int i=0; i<1; i++){
			checker.randomizedSingleRun();
		}
		checker.printResults();
	}

	private void print(Interval<Integer> inter){
		System.out.print("tree.addInterval(new IntegerInterval().create("+(inter.getStart() == null ? "null" : inter.getStart()));
		System.out.println(", "+inter.isStartInclusive()+", "+(inter.getEnd() == null ? "null" : inter.getEnd())+", "+inter.isEndInclusive()+"));");
	}

	private int getRandomInRange(int min, int max){
		return min + (int)(Math.random()*(Math.abs(min)+Math.abs(max)+1));
	}

	private abstract class Checker<T> {
		Configuration config;
		Output out = new Output();

		public Checker(Configuration config){
			this.config = config;
		}

		abstract Set<Interval<Integer>> treeCheck(IntervalTree<Integer> tree, T query);
		abstract boolean listCondition(Interval<Integer> list, T query);
		abstract T createRandom();

		private void randomizedSingleRun(){
			List<Interval<Integer>> list = new ArrayList<>();
			IntervalTree<Integer> tree = new IntervalTree<>();

			for (int i=0; i<config.amount; i++){
				Interval<Integer> next = randomIntervalClosed();
				long time = System.currentTimeMillis();
				list.add(next);
				out.listCreationTime += System.currentTimeMillis() - time;
				time = System.currentTimeMillis();
				tree.addInterval(next);
				out.treeCreationTime += System.currentTimeMillis() - time;
			}

			for (int i=0; i<config.checks; i++){
				T query = createRandom();
				boolean check = check(tree, list, query);
				if (!check){
					System.out.println("Point: "+query);
					for (Interval<Integer> interval: list){
						print(interval);
					}
				}
				assertEquals(true, check);
			}
		}

		private boolean check(IntervalTree<Integer> tree, List<Interval<Integer>> list, T query) {
			long time = System.currentTimeMillis();
			List<Interval<Integer>> fromList = linearCheck(list, query);
			out.listSearchTime += System.currentTimeMillis() - time;
			time = System.currentTimeMillis();
			Set<Interval<Integer>> set = treeCheck(tree, query);
			out.treeSearchTime += System.currentTimeMillis() - time;
			List<Interval<Integer>> fromTree = new ArrayList<>(set);
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

		private List<Interval<Integer>> linearCheck(List<Interval<Integer>> list, T query) {
			List<Interval<Integer>> res = new ArrayList<>();
			for (Interval<Integer> interval: list){
				if (listCondition(interval, query))
					res.add(interval);
			}
			return res;
		}

		Interval<Integer> randomIntervalClosed(){
			int begin = getRandomInRange(-config.range, config.range);
			int length = getRandomInRange(0, config.lengthRange);
			return new IntegerInterval(begin, begin+length, Bounded.CLOSED);
		}

		private Interval<Integer> randomIntervalAllTypes(){
			Interval<Integer> next;
			int begin, length;
			int kind = getRandomInRange(0, 7);
			switch (kind){
				case 0:
					begin = getRandomInRange(-config.range, config.range);
					length = getRandomInRange(0, config.lengthRange);
					next = new IntegerInterval(begin, begin+length, Bounded.CLOSED);
					break;
				case 1:
					begin = getRandomInRange(-config.range, config.range);
					length = getRandomInRange(0, config.lengthRange);
					next = new IntegerInterval(begin, begin+length, Bounded.OPEN);
					break;
				case 2:
					begin = getRandomInRange(-config.range, config.range);
					length = getRandomInRange(0, config.lengthRange);
					next = new IntegerInterval(begin, begin+length, Bounded.CLOSED_LEFT);
					break;
				case 3:
					begin = getRandomInRange(-config.range, config.range);
					length = getRandomInRange(0, config.lengthRange);
					next = new IntegerInterval(begin, begin+length, Bounded.CLOSED_RIGHT);
					break;
				case 4:
					begin = getRandomInRange(-config.range, config.range);
					next = new IntegerInterval(begin, Unbounded.CLOSED_LEFT);
					break;
				case 5:
					begin = getRandomInRange(-config.range, config.range);
					next = new IntegerInterval(begin, Unbounded.OPEN_LEFT);
					break;
				case 6:
					begin = getRandomInRange(-config.range, config.range);
					next = new IntegerInterval(begin, Unbounded.CLOSED_RIGHT);
					break;
				default:
					begin = getRandomInRange(-config.range, config.range);
					next = new IntegerInterval(begin, Unbounded.OPEN_RIGHT);
					break;
			}
			return next;
		}

		public void printResults(){
			System.out.println("List creation time: "+out.listCreationTime);
			System.out.println("List search time: "+out.listSearchTime);
			System.out.println("Tree creation time: "+out.treeCreationTime);
			System.out.println("Tree search time: "+out.treeSearchTime);
		}
	}
}
