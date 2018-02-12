package com.g2forge.gearbox.functional.runner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.io.HIO;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.platform.Platform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcessBuilderRunner implements IRunner {
	@Getter(AccessLevel.PROTECTED)
	protected final IFunction1<? super List<? extends String>, ? extends List<? extends String>> commandFunction;

	public ProcessBuilderRunner() {
		this(Platform.getPlatform().getShell().getCommandNesting());
	}

	@Override
	public IProcess run(Command command) {
		final ProcessBuilder builder = new ProcessBuilder();
		if (command.getWorking() != null) builder.directory(command.getWorking().toFile());

		final IFunction1<? super List<? extends String>, ? extends List<? extends String>> commandFunction = getCommandFunction();
		if (commandFunction != null) builder.command(new ArrayList<>(commandFunction.apply(command.getArguments())));
		else builder.command(command.getArguments());

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
