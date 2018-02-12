package com.g2forge.gearbox.functional.runner;

import java.io.InputStream;

import com.g2forge.alexandria.java.close.ICloseable;

public interface IProcess extends ICloseable {
	public int getExitCode();

	public InputStream getStandardOut();

	public default boolean isSuccess() {
		return getExitCode() == 0;
	}

	public default void assertSuccess() {
		if (!isSuccess()) throw new RuntimeException();
	}
}
