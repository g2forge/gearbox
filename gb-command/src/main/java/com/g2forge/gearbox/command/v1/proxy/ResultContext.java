package com.g2forge.gearbox.command.v1.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.g2forge.alexandria.adt.collection.CircularBuffer;
import com.g2forge.alexandria.command.stdio.IStandardIO;
import com.g2forge.alexandria.command.stdio.StandardIO;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.type.ref.ATypeRef;
import com.g2forge.alexandria.java.type.ref.ATypeRefIdentity;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.gearbox.command.runner.IProcess;
import com.g2forge.gearbox.command.runner.redirect.IRedirect;
import com.g2forge.gearbox.command.runner.redirect.InheritRedirect;
import com.g2forge.gearbox.command.v1.control.IExplicitResultHandler;
import com.g2forge.gearbox.command.v1.control.IResultContext;

import lombok.Data;
import lombok.Getter;

@Data
class ResultContext implements IResultContext {
	protected final Method method;

	@Getter(lazy = true)
	private final ITypeRef<?> type = computeType();

	protected ITypeRef<?> computeType() {
		return new ATypeRefIdentity<Object>() {
			@SuppressWarnings("unchecked")
			@Override
			public Class<Object> getErasedType() {
				return (Class<Object>) method.getReturnType();
			}

			@Override
			public Type getType() {
				return method.getGenericReturnType();
			}
		};
	}

	@Override
	public IExplicitResultHandler getStandard(ITypeRef<?> type) {
		if (type.getErasedType().isAssignableFrom(Boolean.class) || type.getErasedType().isAssignableFrom(Boolean.TYPE)) return (process, context) -> {
			try {
				return process.getExitCode() == 0;
			} finally {
				process.close();
			}
		};
		if (type.getErasedType().isAssignableFrom(Integer.class) || type.getErasedType().isAssignableFrom(Integer.TYPE)) return (process, context) -> {
			try {
				return process.getExitCode();
			} finally {
				process.close();
			}
		};
		if (type.getErasedType().isAssignableFrom(Void.class) || type.getErasedType().isAssignableFrom(Void.TYPE)) return new IExplicitResultHandler() {
			@Override
			public Object apply(IProcess process, IResultContext context) {
				try {
					process.assertSuccess();
					return null;
				} finally {
					process.close();
				}
			}

			@Override
			public IStandardIO<IRedirect, IRedirect> getRedirects() {
				return StandardIO.<IRedirect, IRedirect>builder().standardInput(InheritRedirect.create()).standardOutput(InheritRedirect.create()).standardError(InheritRedirect.create()).build();
			}
		};
		if (type.getErasedType().isAssignableFrom(String.class)) return (process, context) -> {
			try {
				final StringBuilder retVal = new StringBuilder();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getStandardOutput()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						retVal.append(line).append("\n");
					}
				} catch (IOException exception) {
					throw new RuntimeIOException(exception);
				}

				process.assertSuccess();
				return retVal.toString();
			} finally {
				process.close();
			}
		};
		if (new ATypeRef<Stream<String>>() {}.getType().equals(type.getType())) return (process, context) -> {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getStandardOutput()));
			final Iterator<String> iterator = new Iterator<String>() {
				protected boolean done = false;

				protected String line = null;

				protected final CircularBuffer<String> buffer = new CircularBuffer<String>(10);

				protected void close() {
					done = true;
					if (!process.isSuccess()) {
						final List<String> lines = buffer.getList();
						final StringBuilder builder = new StringBuilder().append("Showing last ").append(lines.size()).append(" lines:");
						for (String line : lines) {
							builder.append('\t').append(line).append('\n');
						}
						throw new RuntimeException(builder.toString());
					}
					try {
						reader.close();
					} catch (IOException e) {
						throw new RuntimeIOException(e);
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
		};
		if (type.getErasedType().isAssignableFrom(IProcess.class)) return (process, context) -> process;
		throw new IllegalArgumentException(String.format("Return type \"%1$s\" is not supported!", type.getType()));
	}
}