package com.lodborg.intervaltree;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.lodborg.intervaltree.Interval.*;

import java.util.*;

public class TreeVsListComparisonTest {

	@Ignore @Test
	public void compareRuntime() throws InterruptedException {

		int threadCount = 6;
		int amountPerThread = 100;
		int checksPerThread = 100;
		int range = 10000;
		int lengthRange = 100;

		Vector<Interval<Integer>> storage = new Vector<>();
		Vector<Interval<Integer>> list = new Vector<>();
		ConcurrentIntervalTree<Integer> tree = new ConcurrentIntervalTree<>();

		for (int i=0; i<threadCount*amountPerThread; i++){
			storage.add(createNewRandomInterval(range, lengthRange));
		}

		Thread[] threadsList = new Thread[threadCount];
		Thread[] threadsTree = new Thread[threadCount];

		for (int i=0; i<threadCount; i++){
			threadsList[i] = new Thread(new ListInsertThread(list, storage, i*amountPerThread, (i+1)*amountPerThread));
			threadsTree[i] = new Thread(new TreeInsertThread(tree, storage, i*amountPerThread, (i+1)*amountPerThread));
		}

		long listTime = System.currentTimeMillis();
		for (int i=0; i<threadCount; i++)
			threadsList[i].start();
		for (int i=0; i<threadCount; i++)
			threadsList[i].join();
		listTime = System.currentTimeMillis() - listTime;

		long treeTime = System.currentTimeMillis();
		for (int i=0; i<threadCount; i++)
			threadsTree[i].start();
		for (int i=0; i<threadCount; i++)
			threadsTree[i].join();
		treeTime = System.currentTimeMillis() - treeTime;

		System.out.println("List creation time: "+listTime);
		System.out.println("Tree creation time: "+treeTime);

		Vector<Integer> checkList = new Vector<>();
		for (int i=0; i<checksPerThread*threadCount; i++)
			checkList.add(getRandomInRange(-range, range));
		for (int i=0; i<threadCount; i++){
			threadsList[i] = new Thread(new ListQueryThread(list, checkList, i*checksPerThread, (i+1)*checksPerThread));
			threadsTree[i] = new Thread(new TreeQueryThread(tree, checkList, i*checksPerThread, (i+1)*checksPerThread));
		}


		listTime = System.currentTimeMillis();
		for (int i=0; i<threadCount; i++)
			threadsList[i].start();
		for (int i=0; i<threadCount; i++)
			threadsList[i].join();
		listTime = System.currentTimeMillis() - listTime;

		treeTime = System.currentTimeMillis();
		for (int i=0; i<threadCount; i++)
			threadsTree[i].start();
		for (int i=0; i<threadCount; i++)
			threadsTree[i].join();
		treeTime = System.currentTimeMillis() - treeTime;


		System.out.println("List total time: "+listTime);
		System.out.println("Tree total time: "+treeTime);
	}

	private static class ListInsertThread implements Runnable{
		Vector<Interval<Integer>> storage, list;
		int from, to;

		public ListInsertThread(Vector<Interval<Integer>> list, Vector<Interval<Integer>> storage, int from, int to) {
			this.list = list;
			this.storage = storage;
			this.from = from;
			this.to = to;
		}

		@Override
		public void run() {
			for (int i=from; i<to; i++){
				list.add(storage.get(i));
			}
		}
	}

	private static class ListQueryThread implements Runnable{
		Vector<Interval<Integer>> list;
		Vector<Integer> storage;
		int from, to;

		public ListQueryThread(Vector<Interval<Integer>> list, Vector<Integer> storage, int from, int to) {
			this.list = list;
			this.storage = storage;
			this.from = from;
			this.to = to;
		}

		@Override
		public void run() {
			int count = 0;
			for (int i=from; i<to; i++){
				for (int interval=0; interval < list.size(); interval++)
					if (list.get(interval).contains(storage.get(i)))
						count++;
			}
		}
	}

	private static class TreeInsertThread implements Runnable{
		ConcurrentIntervalTree<Integer> tree;
		Vector<Interval<Integer>> storage;
		int from, to;

		public TreeInsertThread(ConcurrentIntervalTree<Integer> tree, Vector<Interval<Integer>> storage, int from, int to) {
			this.tree = tree;
			this.storage = storage;
			this.from = from;
			this.to = to;
		}

		@Override
		public void run() {
			for (int i=from; i<to; i++){
				try {
					tree.addInterval(storage.get(i));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class TreeQueryThread implements Runnable{
		ConcurrentIntervalTree<Integer> tree;
		Vector<Integer> storage;
		int from, to;

		public TreeQueryThread(ConcurrentIntervalTree<Integer> tree, Vector<Integer> storage, int from, int to) {
			this.tree = tree;
			this.storage = storage;
			this.from = from;
			this.to = to;
		}

		@Override
		public void run() {
			for (int i=from; i<to; i++){
				tree.query(storage.get(i));
			}
		}
	}

	private static Interval<Integer> createNewRandomInterval(int range, int lengthRange){
		int kind = getRandomInRange(0, 7);
		IntegerInterval next;
		int begin, length;
		begin = getRandomInRange(-range, range);
		length = getRandomInRange(0, lengthRange);
		next = new IntegerInterval(begin, begin+length, Bounded.CLOSED);
		/*switch (kind) {
			case 0:
				begin = getRandomInRange(-range, range);
				length = getRandomInRange(0, lengthRange);
				next = new IntegerInterval(begin, begin + length, Bounded.CLOSED);
				break;
			case 1:
				begin = getRandomInRange(-range, range);
				length = getRandomInRange(0, lengthRange);
				next = new IntegerInterval(begin, begin + length, Bounded.OPEN);
				break;
			case 2:
				begin = getRandomInRange(-range, range);
				length = getRandomInRange(0, lengthRange);
				next = new IntegerInterval(begin, begin + length, Bounded.CLOSED_LEFT);
				break;
			case 3:
				begin = getRandomInRange(-range, range);
				length = getRandomInRange(0, lengthRange);
				next = new IntegerInterval(begin, begin + length, Bounded.CLOSED_RIGHT);
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
		return next;
	}

	private static int getRandomInRange(long min, long max){
		return (int)(min + Math.random()*(Math.abs(min)+Math.abs(max)+1));
	}
}
