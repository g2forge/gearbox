package com.g2forge.gearbox.command.proxy.result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.g2forge.alexandria.adt.collection.CircularBuffer;
import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.alexandria.java.io.HIO;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.gearbox.command.process.IProcess;

public class StreamResultSupplier implements IResultSupplier<Stream<String>>, ISingleton {
	protected static final StreamResultSupplier INSTANCE = new StreamResultSupplier();

	public static IResultSupplier<Stream<String>> create() {
		return INSTANCE;
	}

	@Override
	public Stream<String> apply(IProcess process) {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getStandardOutput()));
		final Iterator<String> iterator = new Iterator<String>() {
			protected boolean done = false;

			protected String line = null;

			protected final CircularBuffer<String> buffer = new CircularBuffer<String>(30);

			protected void close() {
				done = true;
				try {
					if (!process.isSuccess()) {
						final List<String> lines = buffer.getList();
						final StringBuilder builder = new StringBuilder().append("Showing last ").append(lines.size()).append(" lines of standard out:\n");
						for (String line : lines) {
							builder.append('\t').append(line).append('\n');
						}
						builder.append("Showing all of standard error:\n");
						HIO.readAll(process.getStandardError()).forEach(line -> builder.append('\t').append(line).append('\n'));
						throw new RuntimeException(builder.toString());
					}
					try {
						reader.close();
					} catch (IOException e) {
						throw new RuntimeIOException(e);
					}
				} finally {
					process.close();
				}
			}

			@Override
			public boolean hasNext() {
				if (done) return false;
				if (line == null) {
					try {
						line = reader.readLine();
						if (line == null) close();
						else buffer.add(line);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
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
}