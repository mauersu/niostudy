package daily.y2016.m01.d29.concurrency;

public class ReentrantLock {
	
	boolean isLocked = false;
	
	Thread lockedBy = null;
	
	int lockedCount = 0;
	
	public synchronized void lock() throws InterruptedException {
		Thread callingThread = Thread.currentThread();
		while(isLocked && lockedBy != callingThread) {
			wait();
		}
		
		isLocked = true;
		lockedCount++;
		lockedBy = callingThread;
	}
	
	public synchronized void unlock() {
		if(Thread.currentThread() == this.lockedBy) {
			lockedCount--;
			
			if(lockedCount==0) {
				isLocked = false;
				notify();
			}
		}
	}
}
