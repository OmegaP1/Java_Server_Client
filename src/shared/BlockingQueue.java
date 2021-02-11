package shared;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<T> {
	private int capacity;
	private Queue<T> queue = new LinkedList<T>();

	public BlockingQueue(int capacity) {
		this.capacity = capacity;
	}

	public BlockingQueue() {
	}

	public synchronized void offer(T t) throws InterruptedException {
		while (capacity > 0 && queue.size() == capacity) {
			wait();
		}
		queue.add(t);
		notifyAll();
	}

	public synchronized T poll() throws InterruptedException {
		while (queue.isEmpty()) {
			wait();
		}
		if (capacity > 0)
			notifyAll();
		return queue.poll();
	}

	public int size() {
		return queue.size();
	}

	public synchronized void clear() {
		queue.clear();
		notifyAll();
	}
}