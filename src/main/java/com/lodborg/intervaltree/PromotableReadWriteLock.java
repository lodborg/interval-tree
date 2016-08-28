package com.lodborg.intervaltree;

public class PromotableReadWriteLock {
	private volatile int readCount;
	private volatile int waitingWrites;
	private volatile boolean isInWritingMode;

	public synchronized void readLock() throws InterruptedException {
		while (waitingWrites > 0 || isInWritingMode)
			wait();
		readCount++;
	}

	public synchronized boolean promoteToWriteIfLast(){
		if (waitingWrites > 0 || readCount > 1 || isInWritingMode)
			return false;
		isInWritingMode = true;
		return true;
	}

	public synchronized void unlock(){
		readCount--;
		isInWritingMode = false;
		notifyAll();
	}

	public synchronized void writeLock() throws InterruptedException {
		waitingWrites++;
		while (readCount > 0 || isInWritingMode)
			wait();
		waitingWrites--;
		readCount++;
		isInWritingMode = true;
	}
}
