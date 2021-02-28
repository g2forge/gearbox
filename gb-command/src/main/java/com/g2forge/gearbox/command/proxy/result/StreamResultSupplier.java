package com.g2forge.gearbox.command.proxy.result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.g2forge.alexandria.adt.collection.CircularBuffer;
import com.g2forge.alexandria.command.stdio.IStandardIO;
import com.g2forge.alexandria.command.stdio.StandardIO;
import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.alexandria.java.concurrent.AThreadActor;
import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.alexandria.java.io.HTextIO;
import com.g2forge.gearbox.command.process.IProcess;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class StreamResultSupplier implements IResultSupplier<Stream<String>>, ISingleton {
	@RequiredArgsConstructor
	protected class IOIterator implements Iterator<String>, ICloseable {
		protected final List<Stream2Queue> threads;

		protected final Queue<String> queue;

		protected final IProcess process;

		protected boolean done = false;

		protected String line = null;

		protected final CircularBuffer<String> buffer = new CircularBuffer<String>(getLines());

		@Override
		public void close() {
			done = true;
			try {
				if (!process.isRunning() && !process.isSuccess()) {
					final List<String> lines = buffer.getList();
					final StringBuilder builder = new StringBuilder().append("Showing last ").append(lines.size()).append(" lines of output:\n");
					final Consumer<? super String> printer = line -> builder.append('\t').append(line).append('\n');
					lines.forEach(printer);

					if (!getInclude().getStandardOutput()) {
						builder.append("Showing all of standard output:\n");
						HTextIO.readAll(process.getStandardError()).forEach(printer);
					}
					if (!getInclude().getStandardError()) {
						builder.append("Showing all of standard error:\n");
						HTextIO.readAll(process.getStandardError()).forEach(printer);
					}

					throw new RuntimeException(builder.toString());
				}
			} finally {
				process.close();
			}
		}

		@Override
		public boolean hasNext() {
			if (done) return false;

			if (threads.isEmpty()) close();
			else if (line == null) {
				threads.removeAll(threads.stream().filter(t -> !t.isOpen()).collect(Collectors.toList()));
				if (threads.isEmpty()) close();
				else {
					// Wait until there's something in the queue
					while (line == null) {
						line = queue.poll();
						if (line != null) break;
						// There wasn't anything so wait for someone to wake us up
						synchronized (queue) {
							try {
								queue.wait();
							} catch (InterruptedException e) {}
						}
					}
					// Notify all the writers that there might be space in the queue
					synchronized (queue) {
						queue.notifyAll();
					}
					if (line != null) buffer.add(line);
				}
			}

			return (line != null);
		}

		@Override
		public String next() {
			if (!hasNext()) throw new NoSuchElementException();
			final String retVal = line;
			line = null;
			return retVal;
		}
	}

	protected static class Stream2Queue extends AThreadActor {
		protected final BufferedReader reader;

		protected final int capacity;

		protected final Queue<String> queue;

		public Stream2Queue(final InputStream stream, int capacity, Queue<String> queue) {
			this.reader = new BufferedReader(new InputStreamReader(stream));
			this.capacity = capacity;
			this.queue = queue;
		}

		@Override
		public Stream2Queue open() {
			return (Stream2Queue) super.open();
		}

		@Override
		protected void run() {
			try {
				if (!isOpen()) return;
				String line = reader.readLine();

				while (line != null) {
					// Wait for there to be space in the queue
					while (queue.size() >= capacity) {
						if (!isOpen()) return;
						synchronized (queue) {
							queue.wait();
						}
					}

					// Add the line and wake up everyone to get the reader to notice there's something there
					synchronized (queue) {
						queue.add(line);
						queue.notifyAll();
					}

					if (!isOpen()) return;
					line = reader.readLine();
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void shutdown() {
			try {
				reader.close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	public static final StreamResultSupplier STANDARD = new StreamResultSupplier(50, new StandardIO<>(null, true, true));

	protected final int lines;

	protected final IStandardIO<Void, Boolean> include;

	@Override
	public Stream<String> apply(IProcess process) {
		final List<Stream2Queue> threads = new ArrayList<>(2);
		final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
		if (getInclude().getStandardOutput()) threads.add(new Stream2Queue(process.getStandardOutput(), 50, queue).open());
		if (getInclude().getStandardError()) threads.add(new Stream2Queue(process.getStandardError(), 50, queue).open());
		final IOIterator iterator = new IOIterator(threads, queue, process);
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL), false).onClose(iterator::close);
	}
}