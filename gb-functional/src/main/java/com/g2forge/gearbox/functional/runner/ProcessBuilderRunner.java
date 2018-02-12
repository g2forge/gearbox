package com.g2forge.gearbox.functional.runner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.io.HIO;
import com.g2forge.alexandria.java.io.RuntimeIOException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcessBuilderRunner implements IRunner {
	@Getter(AccessLevel.PROTECTED)
	protected final List<String> prefix;

	public ProcessBuilderRunner(String... prefix) {
		this(HCollection.asList(prefix));
	}

	protected List<String> getArguments(Command command) {
		final List<String> prefix = getPrefix();
		if ((prefix == null) || prefix.isEmpty()) return command.getArguments();
		final List<String> retVal = new ArrayList<>();
		retVal.addAll(prefix);
		retVal.addAll(command.getArguments());
		return retVal;
	}

	@Override
	public IProcess run(Command command) {
		final ProcessBuilder builder = new ProcessBuilder();
		if (command.getWorking() != null) builder.directory(command.getWorking().toFile());

		builder.command(getArguments(command));

		final Process process;
		try {
			process = builder.start();
		} catch (IOException exception) {
			throw new RuntimeIOException(exception);
		}
		return new IProcess() {
			@Getter(lazy = true)
			private final int exitCode = computeExitCode();

			@Override
			public void close() {
				HIO.closeAll(process.getInputStream(), process.getErrorStream(), process.getOutputStream());
			}

			protected int computeExitCode() {
				try {
					return process.waitFor();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public InputStream getStandardOut() {
				return process.getInputStream();
			}
		};
	}

}
