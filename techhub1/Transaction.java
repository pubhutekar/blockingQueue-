package org.techhub1;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

class Transaction {
	private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
	private final String transactionId;
	private final long timestamp;
	private final String type;
	private final double amount;

	public Transaction(String type, double amount) {
		this.transactionId = String.valueOf(ID_GENERATOR.incrementAndGet());
		this.timestamp = System.currentTimeMillis();
		this.type = type;
		this.amount = amount;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getType() {
		return type;
	}

	public double getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return "Transaction{" + "transactionId='" + transactionId + '\'' + ", timestamp=" + timestamp + ", type='"
				+ type + '\'' + ", amount=" + amount + '}';
	}
}

class TransactionHistory implements List<Transaction> {
	private final List<Transaction> transactions = new ArrayList<>();

	@Override
	public synchronized boolean add(Transaction transaction) {
		return transactions.add(transaction);
	}

	@Override
	public synchronized Transaction get(int index) {
		return transactions.get(index);
	}

	@Override
	public synchronized int size() {
		return transactions.size();
	}

	@Override
	public synchronized Transaction remove(int index) {
		return transactions.remove(index);
	}

	

	@Override
	public synchronized boolean isEmpty() {
		return transactions.isEmpty();
	}

	@Override
	public synchronized void clear() {
		transactions.clear();
	}

	@Override
	public synchronized boolean contains(Object o) {
		return transactions.contains(o);
	}

	@Override
	public synchronized boolean containsAll(java.util.Collection<?> c) {
		return transactions.containsAll(c);
	}

	@Override
	public synchronized boolean addAll(java.util.Collection<? extends Transaction> c) {
		return transactions.addAll(c);
	}

	@Override
	public synchronized boolean addAll(int index, java.util.Collection<? extends Transaction> c) {
		return transactions.addAll(index, c);
	}

	@Override
	public synchronized boolean remove(Object o) {
		return transactions.remove(o);
	}

	@Override
	public synchronized boolean removeAll(java.util.Collection<?> c) {
		return transactions.removeAll(c);
	}

	@Override
	public synchronized boolean retainAll(java.util.Collection<?> c) {
		return transactions.retainAll(c);
	}

	@Override
	public synchronized Object[] toArray() {
		return transactions.toArray();
	}

	@Override
	public synchronized <T> T[] toArray(T[] a) {
		return transactions.toArray(a);
	}

	@Override
	public synchronized void add(int index, Transaction element) {
		transactions.add(index, element);
	}

	@Override
	public synchronized int indexOf(Object o) {
		return transactions.indexOf(o);
	}

	@Override
	public synchronized int lastIndexOf(Object o) {
		return transactions.lastIndexOf(o);
	}

	@Override
	public synchronized ListIterator<Transaction> listIterator() {
		return transactions.listIterator();
	}

	@Override
	public synchronized ListIterator<Transaction> listIterator(int index) {
		return transactions.listIterator(index);
	}

	@Override
	public synchronized java.util.Iterator<Transaction> iterator() {
		return transactions.iterator();
	}

	@Override
	public synchronized List<Transaction> subList(int fromIndex, int toIndex) {
		return transactions.subList(fromIndex, toIndex);
	}

	@Override
	public synchronized Transaction set(int index, Transaction element) {
		return transactions.set(index, element);
	}
}

class TransactionProcessor {
	private final BlockingQueue<Transaction> queue;
	private final TransactionHistory history;
	private final int producerCount;
	private final int consumerCount;

	public TransactionProcessor(int queueSize, int producerCount, int consumerCount) {
		this.queue = new ArrayBlockingQueue<>(queueSize);
		this.history = new TransactionHistory();
		this.producerCount = producerCount;
		this.consumerCount = consumerCount;
	}

	public void startProcessing() {
		for (int i = 0; i < producerCount; i++) {
			new Thread(new Producer(queue)).start();
		}

		for (int i = 0; i < consumerCount; i++) {
			new Thread(new Consumer(queue, history)).start();
		}
	}

	class Producer implements Runnable {
		private final BlockingQueue<Transaction> queue;

		public Producer(BlockingQueue<Transaction> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {
			try {
				while (true) {
					Transaction transaction = new Transaction("type", Math.random() * 1000);
					queue.put(transaction);
					System.out.println("Produced: " + transaction);
					Thread.sleep((int) (Math.random() * 1000));
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	class Consumer implements Runnable {
		private final BlockingQueue<Transaction> queue;
		private final TransactionHistory history;

		public Consumer(BlockingQueue<Transaction> queue, TransactionHistory history) {
			this.queue = queue;
			this.history = history;
		}

		@Override
		public void run() {
			try {
				while (true) {
					Transaction transaction = queue.take();
					System.out.println("Consumed: " + transaction);
					history.add(transaction);
					// Process the transaction (e.g., log it, save to a database, etc.)
					Thread.sleep((int) (Math.random() * 1000));
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static void main(String[] args) {
		TransactionProcessor processor = new TransactionProcessor(10, 5, 5);
		processor.startProcessing();
	}
}
