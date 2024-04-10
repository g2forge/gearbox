package com.g2forge.gearbox.command.log;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.command.invocation.environment.IEnvironment;
import com.g2forge.alexandria.command.invocation.environment.MapEnvironment;
import com.g2forge.alexandria.command.invocation.environment.SystemEnvironment;
import com.g2forge.alexandria.command.invocation.environment.modified.IEnvironmentModifier;
import com.g2forge.alexandria.command.invocation.environment.modified.ModifiedEnvironment;
import com.g2forge.alexandria.java.core.error.NotYetImplementedError;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.IRunner;
import com.g2forge.gearbox.command.process.redirect.IRedirect;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class LoggingRunner implements IRunner {
	protected static String toString(IEnvironment environment, String prefix) {
		final StringBuilder retVal = new StringBuilder();
		if (environment instanceof MapEnvironment) {
			for (Map.Entry<String, String> entry : ((MapEnvironment) environment).getVariables().entrySet()) {
				retVal.append(prefix).append(entry.getKey()).append('=').append(entry.getValue()).append("\n");
			}
		} else if (environment instanceof ModifiedEnvironment) {
			final ModifiedEnvironment modified = ((ModifiedEnvironment) environment);

			final String base = toString(modified.getBase(), prefix + "\t");
			final boolean multiLineBase = base.contains("\n");
			final int modifierCount = modified.getModifiers().size();

			if (multiLineBase || modifierCount > 1) {
				retVal.append(prefix).append("modified:").append(multiLineBase ? '\n' : ' ').append(multiLineBase ? base : base.strip()).append('\n');
				retVal.append(prefix).append("modifiers:\n");
				for (Map.Entry<String, IEnvironmentModifier> entry : modified.getModifiers().entrySet()) {
					retVal.append(prefix).append('\t').append(entry.getKey()).append(" <- ").append(entry.getValue()).append('\n');
				}
			} else if (modifierCount < 1) retVal.append(base.strip());
			else {
				final Entry<String, IEnvironmentModifier> entry = HCollection.getOne(modified.getModifiers().entrySet());
				retVal.append(prefix).append("modified ").append(base.strip()).append(" with ").append(entry.getKey()).append(" <- ").append(entry.getValue());
			}
		} else if (environment instanceof SystemEnvironment) {
			retVal.append(prefix).append("system environment\n");
		} else throw new NotYetImplementedError("Environments of type " + environment.getClass() + " are not yet supported!");

		return retVal.toString().stripTrailing();
	}

	protected final IConsumer1<String> log;

	protected final IRunner runner;

	@Override
	public IProcess apply(CommandInvocation<IRedirect, IRedirect> commandInvocation) {
		final IConsumer1<String> log = getLog();
		log.accept("Running: " + commandInvocation.getArguments().stream().collect(Collectors.joining(" ")));
		if (commandInvocation.getWorking() != null) log.accept("\tin " + commandInvocation.getWorking());
		final IEnvironment environment = commandInvocation.getEnvironment();
		if ((environment != null) && !(commandInvocation.getEnvironment() instanceof SystemEnvironment)) {
			final String string = toString(environment, "\t\t");
			if (string.contains("\n")) {
				log.accept("\tenvironment:");
				for (String line : string.split("\n")) {
					log.accept(line);
				}
			} else log.accept("\tenvironment: " + string.substring(2 /* Remove the tabs */));
		}
		return runner.apply(commandInvocation);
	}
}