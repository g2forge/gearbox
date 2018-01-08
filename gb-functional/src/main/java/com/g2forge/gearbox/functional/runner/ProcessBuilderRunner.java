package com.g2forge.gearbox.functional.runner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.g2forge.alexandria.java.io.RuntimeIOException;

public class ProcessBuilderRunner implements IRunner {
	@Override
	public IProcess run(List<String> arguments) {
		final ProcessBuilder builder = new ProcessBuilder(arguments);
		final Process process;
		try {
			process = builder.start();
		} catch (IOException exception) {
			throw new RuntimeIOException(exception);
		}
		return new IProcess() {
			@Override
			public InputStream getStandardOut() {
				return process.getInputStream();
			}
		};
	}

}
