package com.g2forge.gearbox.command.process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.Map;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.command.invocation.runner.ICommandRunner;
import com.g2forge.alexandria.command.process.HProcess;
import com.g2forge.alexandria.command.stdio.IStandardIO;
import com.g2forge.alexandria.command.stdio.StandardIO;
import com.g2forge.alexandria.command.stdio.StandardIO.StandardIOBuilder;
import com.g2forge.alexandria.java.function.IFunction2;
import com.g2forge.alexandria.java.io.HIO;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.type.function.TypeSwitch2;
import com.g2forge.gearbox.command.process.redirect.FileRedirect;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.process.redirect.InheritRedirect;
import com.g2forge.gearbox.command.process.redirect.PipeRedirect;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.proxy.process.environment.IEnvironmentValue;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcessBuilderRunner implements IRunner {
	protected static final IFunction2<IRedirect, Boolean, Redirect> redirectTranslater = new TypeSwitch2.FunctionBuilder<IRedirect, Boolean, Redirect>().with(builder -> {
		builder.add(InheritRedirect.class, Boolean.class, (r, o) -> Redirect.INHERIT);
		builder.add(PipeRedirect.class, Boolean.class, (r, o) -> Redirect.PIPE);
		builder.add(FileRedirect.class, Boolean.class, (r, o) -> {
			final File file = r.getPath().toFile();
			if (o) return r.isAppend() ? Redirect.appendTo(file) : Redirect.to(file);
			return Redirect.from(file);
		});
	}).build();

	@Getter(AccessLevel.PROTECTED)
	protected final ICommandRunner commandRunner;

	public ProcessBuilderRunner() {
		this(ICommandRunner.create(null));
	}

	@Override
	public IProcess apply(ProcessInvocation<?> processInvocation) {
		final CommandInvocation<IRedirect, IRedirect> commandInvocation = processInvocation.getCommandInvocation();
		if (commandInvocation == null) return null;
		final CommandInvocation<IRedirect, IRedirect> wrapped = getCommandRunner().wrap(commandInvocation);

		final CommandInvocation<ProcessBuilder.Redirect, ProcessBuilder.Redirect> translated;
		{ // Translate the redirects
			final CommandInvocation.CommandInvocationBuilder<ProcessBuilder.Redirect, ProcessBuilder.Redirect> invocationBuilder = CommandInvocation.builder();
			invocationBuilder.format(wrapped.getFormat());
			invocationBuilder.working(wrapped.getWorking());
			invocationBuilder.arguments(wrapped.getArguments());

			final IStandardIO<IRedirect, IRedirect> redirects = wrapped.getIo();
			if (redirects != null) {
				final StandardIOBuilder<Redirect, Redirect> stdioBuilder = StandardIO.<ProcessBuilder.Redirect, ProcessBuilder.Redirect>builder();
				final IRedirect standardInput = redirects.getStandardInput();
				if (standardInput != null) stdioBuilder.standardInput(redirectTranslater.apply(standardInput, false));
				final IRedirect standardOutput = redirects.getStandardOutput();
				if (standardOutput != null) stdioBuilder.standardOutput(redirectTranslater.apply(standardOutput, true));
				final IRedirect standardError = redirects.getStandardError();
				if (standardError != null) stdioBuilder.standardError(redirectTranslater.apply(standardError, true));
				invocationBuilder.io(stdioBuilder.build());
			}

			translated = invocationBuilder.build();
		}

		// Create the process builder
		final ProcessBuilder builder = HProcess.createProcessBuilder(translated);

		// Setup the environment variables
		if (processInvocation.getEnvironmentVariables() != null) {
			final Map<String, String> environment = builder.environment();
			for (Map.Entry<String, IEnvironmentValue> entry : processInvocation.getEnvironmentVariables().entrySet()) {
				final String name = entry.getKey();

				final String parent = environment.get(name);
				final String child = entry.getValue().modify(parent);
				if (child == null) environment.remove(name);
				else environment.put(name, child);
			}
		}

		// Start the process
		final Process process;
		try {
			process = builder.start();
		} catch (IOException exception) {
			throw new RuntimeIOException(exception);
		}

		// Generate a process object so the caller can access it
		return new IProcess() {
			@Getter(lazy = true)
			private final int exitCode = computeExitCode();

			@Override
			public void close() {
				process.descendants().forEach(handle -> handle.destroy());
				process.destroy();
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
			public InputStream getStandardError() {
				return process.getErrorStream();
			}

			@Override
			public OutputStream getStandardInput() {
				return process.getOutputStream();
			}

			@Override
			public InputStream getStandardOutput() {
				return process.getInputStream();
			}

			@Override
			public boolean isRunning() {
				return process.isAlive();
			}
		};
	}
}
