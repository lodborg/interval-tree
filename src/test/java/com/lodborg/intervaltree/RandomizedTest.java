package com.lodborg.intervaltree;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.lodborg.intervaltree.Interval.*;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import static org.junit.Assert.assertEquals;

public class RandomizedTest {

	@Test
	@Ignore
	public void benchmark() throws IOException {
		Configuration config = new Configuration();
		config.range = 100_000_000;
		config.lengthRange = 200;
		config.checks = 2000;

		int[] x = new int[]{
			50_000, 100_000, 250_000, 500_000, 1_000_000, 2_300_000, 5_000_000
		};
		int[] lengths = new int[]{
			200, 2_000, 20_000, 200_000
		};
		int[][] y = new int[lengths.length][x.length];

		for (int j=0; j<lengths.length; j++) {
			config.lengthRange = lengths[j];
			for (int i = 0; i < x.length; i++) {
				config.amount = x[i];
				TimeLimitedChecker<Integer> checker = new TimeLimitedChecker<Integer>(config, 10) {
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

				checker.run();
				y[j][i] = checker.countOperationsTree / checker.countOperationsList;
			}
		}

		XYChart chart = new XYChartBuilder().width(500).height(375).title("")
				.xAxisTitle("Size of the tree, logarithmic scale")
				.yAxisTitle("tree/list lookups per second").build();

		chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
		chart.getStyler().setAxisTitlesVisible(true);
		chart.getStyler().setXAxisLogarithmic(true);
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

		for (int j=0; j<lengths.length; j++){
			int ratio = 2*config.range/lengths[j];
			chart.addSeries("RL ratio 1:"+ratio, x, y[j]);
		}

		BitmapEncoder.saveBitmap(chart, "Sample_Chart", BitmapEncoder.BitmapFormat.PNG);
	}

	@Ignore
	@Test
	public void randomized_interval(){
		Configuration config = new Configuration();
		config.amount = 500_000;
		config.range = 100_000_000;
		config.lengthRange = 200;
		config.checks = 500;
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

		checker.run();
		checker.printResults();
	}

	@Test
	@Ignore
	public void randomized_point(){
		Configuration config = new Configuration();
		config.amount = 1_000_000;
		config.range = 100_000_000;
		config.lengthRange = 200;
		config.checks = 100;

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

		checker.run();
		checker.printResults();
	}

	private class Configuration {
		int amount, range, lengthRange, checks;
	}

	private class Output {
		long listCreationTime, listSearchTime, treeCreationTime, treeSearchTime;
	}

	private void print(Interval<Integer> inter){
		System.out.print("tree.add(new IntegerInterval().create("+(inter.getStart() == null ? "null" : inter.getStart()));
		System.out.println(", "+inter.isStartInclusive()+", "+(inter.getEnd() == null ? "null" : inter.getEnd())+", "+inter.isEndInclusive()+"));");
	}

	private int getRandomInRange(int min, int max){
		return min + (int)(Math.random()*(Math.abs(min)+Math.abs(max)+1));
	}

	private abstract class TimeLimitedChecker<T> extends Checker<T>{
		int timeLimitInSeconds;
		int countOperationsTree;
		int countOperationsList;

		public TimeLimitedChecker(Configuration config, int timeLimitInSeconds) {
			super(config);
			this.timeLimitInSeconds = timeLimitInSeconds;
		}

		@Override
		protected void runChecks(){
			countOperationsList = 0;
			countOperationsTree = 0;

			long limit = System.currentTimeMillis() + timeLimitInSeconds*1000;
			while (System.currentTimeMillis() < limit){
				T query = createRandom();
				performLookupTree(query);
				countOperationsTree++;
			}

			limit = System.currentTimeMillis() + timeLimitInSeconds * 1000;
			while (System.currentTimeMillis() < limit){
				T query = createRandom();
				performLookupList(query);
				countOperationsList++;
			}
		}

		@Override
		public void printResults() {
			System.out.println("Count tree operations: "+countOperationsTree);
			System.out.println("Count list operations: "+countOperationsList);
		}
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
		List<Interval<Integer>> list = new ArrayList<>();
		IntervalTree<Integer> tree = new IntervalTree<>();

		public void run(){
			prepareDataStructures();
			runChecks();
		}

		protected void prepareDataStructures(){
			for (int i=0; i<config.amount; i++){
				Interval<Integer> next = randomIntervalClosed();
				long time = System.currentTimeMillis();
				list.add(next);
				out.listCreationTime += System.currentTimeMillis() - time;
				time = System.currentTimeMillis();
				tree.add(next);
				out.treeCreationTime += System.currentTimeMillis() - time;
			}
		}

		protected void runChecks(){
			for (int i=0; i<config.checks; i++){
				T query = createRandom();
				boolean check = check(query);
				if (!check){
					System.out.println("Point: "+query);
					for (Interval<Integer> interval: list){
						print(interval);
					}
				}
				assertEquals(true, check);
			}
		}

		protected List<Interval<Integer>> performLookupList(T query){
			long time = System.currentTimeMillis();
			List<Interval<Integer>> fromList = linearCheck(list, query);
			out.listSearchTime += System.currentTimeMillis() - time;
			return fromList;
		}

		protected Set<Interval<Integer>> performLookupTree(T query){
			long time = System.currentTimeMillis();
			Set<Interval<Integer>> set = treeCheck(tree, query);
			out.treeSearchTime += System.currentTimeMillis() - time;
			return set;
		}

		private boolean check(T query) {
			List<Interval<Integer>> fromList = performLookupList(query);
			List<Interval<Integer>> fromTree = new ArrayList<>(performLookupTree(query));
			Collections.sort(fromList, Interval.sweepLeftToRight);
			Collections.sort(fromTree, Interval.sweepLeftToRight);
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
