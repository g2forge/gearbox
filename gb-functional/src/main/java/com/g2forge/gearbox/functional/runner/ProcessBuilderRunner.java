package com.g2forge.gearbox.functional.runner;

import java.io.IOException;
import java.io.InputStream;

import com.g2forge.alexandria.java.io.HIO;
import com.g2forge.alexandria.java.io.RuntimeIOException;

import lombok.Getter;

public class ProcessBuilderRunner implements IRunner {
	@Override
	public IProcess run(Command command) {
		final ProcessBuilder builder = new ProcessBuilder(command.getArguments());
		if (command.getWorking() != null) builder.directory(command.getWorking().toFile());
		final Process process;
		try {
			process = builder.start();
		} catch (IOException exception) {
			throw new RuntimeIOException(exception);
		}
		return new IProcess() {
			@Getter(lazy = true)
			private final int exitCode = computeExitCode();

			protected int computeExitCode() {
				try {
					return process.waitFor();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void close() {
				HIO.closeAll(process.getInputStream(), process.getErrorStream(), process.getOutputStream());
			}

			@Override
			public InputStream getStandardOut() {
				return process.getInputStream();
			}
		};
	}

}
