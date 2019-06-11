package com.g2forge.gearbox.command.v1.runner;

import java.io.InputStream;
import java.io.OutputStream;

import com.g2forge.alexandria.command.stdio.IStandardIO;
import com.g2forge.alexandria.java.close.ICloseable;

public interface IProcess extends ICloseable, IStandardIO<OutputStream, InputStream> {
	public int getExitCode();

	public default boolean isSuccess() {
		return getExitCode() == 0;
	}

	public default void assertSuccess() {
		if (!isSuccess()) throw new RuntimeException();
	}
}
