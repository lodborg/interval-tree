package com.lodborg.intervaltree;

import java.util.ArrayList;
import java.util.List;

public class ConcurrentIntervalTree<T extends Comparable<? super T>> {
	private volatile ConcurrentTreeNode<T> root;
	private PromotableReadWriteLock lock = new PromotableReadWriteLock();

	@SuppressWarnings("Duplicates")
	public void addInterval(Interval<T> interval) throws InterruptedException {
		if (interval.isEmpty())
			return;
		if (root == null){
			lock.writeLock();
			if (root == null){
				root = new ConcurrentTreeNode<>(interval);
				lock.unlock();
				return;
			}
			lock.unlock();
		}

		lock.readLock();
		root.addInterval(interval);
		if (lock.promoteToWriteIfLast()){
			root = root.balanceOut();
		}
		lock.unlock();
	}

	public List<Interval<T>> query(T point){
		return ConcurrentTreeNode.query(root, point, new ArrayList<>());
	}

	public void removeInterval(Interval<T> interval) throws InterruptedException {
		if (interval.isEmpty())
			return;
		lock.readLock();
		if (root == null){
			lock.unlock();
			return;
		}
		if (root.removeInterval(interval)){
			if (lock.promoteToWriteIfLast()){
				root = root.balanceOut();
			}
		}
		lock.unlock();
	}
}
