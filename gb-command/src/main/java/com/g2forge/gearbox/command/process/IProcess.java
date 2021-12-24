package com.g2forge.gearbox.command.process;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.g2forge.alexandria.command.stdio.IStandardIO;
import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.alexandria.java.core.error.HError;
import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.java.io.HTextIO;

public interface IProcess extends ICloseable, IStandardIO<OutputStream, InputStream> {
	public default void assertSuccess() {
		if (!isSuccess()) {
			final StringBuilder builder = new StringBuilder();
			final List<Throwable> throwables = new ArrayList<>();
			final IConsumer1<? super String> consumer = line -> builder.append('\t').append(line).append('\n');
			builder.append("Standard output:\n");
			try {
				HTextIO.readAll(getStandardOutput(), consumer);
			} catch (Throwable throwable) {
				throwables.add(throwable);
			}
			builder.append("Standard error:\n");
			try {
				HTextIO.readAll(getStandardError(), consumer);
			} catch (Throwable throwable) {
				throwables.add(throwable);
			}
			throw HError.withSuppressed(new RuntimeException(builder.toString()), throwables);
		}
	}

	public int getExitCode();

	public boolean isRunning();

	public default boolean isSuccess() {
		return getExitCode() == 0;
	}
}
