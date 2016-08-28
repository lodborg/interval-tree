package com.lodborg.intervaltree;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.lodborg.intervaltree.Interval.*;

import java.util.*;

public class ConcurrentIntervalTreeTest {
	static int amount = 100000;
	static int range = 1000;
	static int lengthRange = 100;
	static int checks = 1000;
	static int threadCount = 6;

	static volatile long treeCreationTime;
	static long normalCreationTime;
	static long elapsedTime;
	static Vector<Interval<Integer>> vector;

	@Test
	public void randomized() throws InterruptedException {
		ConcurrentIntervalTree<Integer> conTree = new ConcurrentIntervalTree<>();
		vector = new Vector<>();
		IntervalTree<Integer> tree = new IntervalTree<>();
		Thread[] threads = new Thread[threadCount];

		for (int i=0; i<threadCount; i++){
			threads[i] = new Thread(new InsertionTest(conTree, vector));
		}
		elapsedTime = System.currentTimeMillis();
		for (Thread thread: threads) {
			thread.start();
		}
		for (Thread thread: threads)
			thread.join();
		elapsedTime = System.currentTimeMillis() - elapsedTime;

		for (Interval<Integer> interval: vector) {
			long time = System.currentTimeMillis();
			tree.addInterval(interval);
			normalCreationTime += System.currentTimeMillis() - time;
		}

		for (int i=0; i<checks; i++){
			assertEquals(true, check(conTree, tree));
		}

		System.out.println("Concurrent creation time over all threads: "+treeCreationTime);
		System.out.println("Elapsed time: "+elapsedTime);
		System.out.println("One thread tree creation time: "+normalCreationTime);
	}

	private boolean check(ConcurrentIntervalTree<Integer> conTree, IntervalTree<Integer> tree){
		Integer point = getRandomInRange(-range, range);
		List<Interval<Integer>> fromConcurrent = conTree.query(point);
		List<Interval<Integer>> fromTree = tree.query(point);
		Collections.sort(fromConcurrent, Interval.startComparator);
		Collections.sort(fromTree, Interval.startComparator);
		int treeIndex = 0;
		if (fromConcurrent.size() != fromTree.size())
			return false;
		for (int j=0; j<fromConcurrent.size(); j++)
			if (!fromConcurrent.get(j).equals(fromTree.get(j))) {
				return false;
			}
		return true;
	}

	private static class InsertionTest implements Runnable{
		ConcurrentIntervalTree<Integer> tree;
		Vector<Interval<Integer>> vector;

		public InsertionTest(ConcurrentIntervalTree<Integer> tree, Vector<Interval<Integer>> vector){
			this.tree = tree;
			this.vector = vector;
		}

		@Override
		public void run() {
			for (int i=0; i<amount; i++){
				int kind = getRandomInRange(0, 7);
				IntegerInterval next;
				int begin, length;
				begin = getRandomInRange(-range, range);
				length = getRandomInRange(0, lengthRange);
				next = new IntegerInterval(begin, begin+length, Bounded.CLOSED);
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
				}*/

				vector.add(next);
				long time = System.currentTimeMillis();
				try {
					tree.addInterval(next);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				time = System.currentTimeMillis() - time;
				synchronized (ConcurrentIntervalTreeTest.class){
					treeCreationTime += time;
				}
			}
		}
	}

	private static int getRandomInRange(long min, long max){
		return (int)(min + Math.random()*(Math.abs(min)+Math.abs(max)+1));
	}
}
