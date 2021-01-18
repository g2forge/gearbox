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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.g2forge.alexandria.adt.collection.CircularBuffer;
import com.g2forge.alexandria.command.stdio.IStandardIO;
import com.g2forge.alexandria.command.stdio.StandardIO;
import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.alexandria.java.function.IRunnable;
import com.g2forge.alexandria.java.io.HTextIO;
import com.g2forge.gearbox.command.process.IProcess;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class StreamResultSupplier implements IResultSupplier<Stream<String>>, ISingleton {
	public static final StreamResultSupplier STANDARD = new StreamResultSupplier(50, new StandardIO<>(null, true, true));

	protected final int lines;

	protected final IStandardIO<Void, Boolean> include;

	@Override
	public Stream<String> apply(IProcess process) {
		final List<Stream2Queue> queues = new ArrayList<>(2);
		if (getInclude().getStandardOutput()) queues.add(new Stream2Queue(process.getStandardOutput(), 50).start());
		if (getInclude().getStandardError()) queues.add(new Stream2Queue(process.getStandardError(), 50).start());

		final Iterator<String> iterator = new Iterator<String>() {
			protected int index = 0;

			protected boolean done = false;

			protected String line = null;

			protected final CircularBuffer<String> buffer = new CircularBuffer<String>(getLines());

			protected void close() {
				done = true;
				try {
					if (!process.isSuccess()) {
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

				if (queues.isEmpty()) close();
				else if (line == null) {
					final int startIndex = index;
					while (true) {
						final Stream2Queue queue = queues.get(index);
						if (!queue.isOpen()) {
							queues.remove(index);
							queue.close();
							if (queues.isEmpty()) {
								close();
								break;
							}
						}

						line = queue.poll();
						if (line == null) {
							index = (index + 1) % queues.size();
							if (index != startIndex) continue;
						} else {
							buffer.add(line);
							break;
						}
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
		};
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL), false);
	}

	protected static class Stream2Queue implements ICloseable {
		protected volatile transient boolean open = true;

		protected final Queue<String> queue;

		protected final BufferedReader reader;

		protected final int capacity;

		public Stream2Queue(final InputStream stream, int capacity) {
			this.queue = new ConcurrentLinkedQueue<>();
			this.reader = new BufferedReader(new InputStreamReader(stream));
			this.capacity = capacity;
		}

		public synchronized Stream2Queue start() {
			new Thread(new IRunnable() {
				@Override
				public void run() {
					try {
						if (!isOpen()) return;
						String line = reader.readLine();

						while (line != null) {
							while (queue.size() >= capacity) {
								if (!isOpen()) return;
								synchronized (Stream2Queue.this) {
									Stream2Queue.this.wait();
								}
							}

							queue.add(line);

							if (!isOpen()) return;
							line = reader.readLine();
						}
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					close();
				}
			}).start();
			return this;
		}

		public boolean isOpen() {
			return open;
		}

		public String poll() {
			synchronized (this) {
				notifyAll();
			}
			return queue.poll();
		}

		@Override
		public synchronized void close() {
			try {
				reader.close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			open = false;
		}
	}
}