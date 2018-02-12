package com.g2forge.gearbox.functional.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.typed.ATypeRef;
import com.g2forge.alexandria.java.typed.ATypeRefIdentity;
import com.g2forge.alexandria.java.typed.ITypeRef;
import com.g2forge.gearbox.functional.control.IExplicitResultHandler;
import com.g2forge.gearbox.functional.control.IResultContext;
import com.g2forge.gearbox.functional.runner.IProcess;

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
		if (type.getErasedType().isAssignableFrom(String.class)) return (process, context) -> {
			try {
				final StringBuilder retVal = new StringBuilder();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getStandardOut()))) {
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
			final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getStandardOut()));
			final Iterator<String> iterator = new Iterator<String>() {
				protected boolean done = false;

				protected String line = null;

				protected void close() {
					done = true;
					process.assertSuccess();
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