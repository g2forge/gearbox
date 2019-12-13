package com.g2forge.gearbox.command.process;

import java.io.InputStream;
import java.io.OutputStream;

import com.g2forge.alexandria.command.stdio.IStandardIO;
import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.alexandria.java.io.HIO;

public interface IProcess extends ICloseable, IStandardIO<OutputStream, InputStream> {
	public default void assertSuccess() {
		if (!isSuccess()) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Standard output:\n");
			HIO.readAll(getStandardOutput()).forEach(line -> builder.append('\t').append(line).append('\n'));
			builder.append("Standard error:\n");
			HIO.readAll(getStandardError()).forEach(line -> builder.append('\t').append(line).append('\n'));
			throw new RuntimeException(builder.toString());
		}
	}

	public int getExitCode();

	public default boolean isSuccess() {
		return getExitCode() == 0;
	}
}
