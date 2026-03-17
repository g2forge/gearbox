package com.g2forge.gearbox.command.log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.g2forge.alexandria.command.invocation.CommandArgument;
import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.command.invocation.environment.EnvironmentHandler;
import com.g2forge.alexandria.command.invocation.environment.IEnvironment;
import com.g2forge.alexandria.command.invocation.environment.MapEnvironment;
import com.g2forge.alexandria.command.invocation.environment.SystemEnvironment;
import com.g2forge.alexandria.command.invocation.environment.modified.IEnvironmentModifier;
import com.g2forge.alexandria.command.invocation.environment.modified.ModifiedEnvironment;
import com.g2forge.alexandria.java.core.error.NotYetImplementedError;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.helpers.HMap;
import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.text.HString;
import com.g2forge.gearbox.command.converter.MetadataEnvironmentModifier;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.IRunner;
import com.g2forge.gearbox.command.process.MetaCommandArgument;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class LoggingRunner implements IRunner {
	protected static ILogArgumentRewriter computeLogArgumentRewriter(final ISubject subject) {
		if (subject == null) return NopLogArgumentRewriter.create();
		return subject.bind(ILogArgumentRewriter.class).or(NopLogArgumentRewriter.create());
	}

	protected static ILogArgumentRewriter computeLogArgumentRewriter(final MetadataEnvironmentModifier mem) {
		MetadataEnvironmentModifier current = mem;
		final List<ILogArgumentRewriter> logArgumentRewriters = new ArrayList<>();
		while (true) {
			final ILogArgumentRewriter logArgumentRewriter = computeLogArgumentRewriter(current.getSubject());
			if (logArgumentRewriter != null) logArgumentRewriters.add(logArgumentRewriter);

			if (current.getModifier() instanceof MetadataEnvironmentModifier) current = (MetadataEnvironmentModifier) current.getModifier();
			else break;
		}

		return new ChainedLogArgumentRewriter(logArgumentRewriters);
	}

	@EnvironmentHandler
	protected static String toString(IEnvironment environment, String prefix, Map<String, ILogArgumentRewriter> logArgumentRewriterMap, Map<String, Object> context) {
		final StringBuilder retVal = new StringBuilder();
		if (environment instanceof MapEnvironment) {
			for (Map.Entry<String, String> entry : ((MapEnvironment) environment).getVariables().entrySet()) {
				final ILogArgumentRewriter log = logArgumentRewriterMap.getOrDefault(entry.getKey(), NopLogArgumentRewriter.create());
				final String rewritten = log.rewrite(entry.getValue(), context);
				if (rewritten != null) retVal.append(prefix).append(entry.getKey()).append('=').append(rewritten).append("\n");
			}
		} else if (environment instanceof ModifiedEnvironment) {
			final ModifiedEnvironment modified = ((ModifiedEnvironment) environment);

			final Map<String, ILogArgumentRewriter> childLogArgumentRewriterMap = new LinkedHashMap<>(logArgumentRewriterMap);
			for (Map.Entry<String, IEnvironmentModifier> entry : modified.getModifiers().entrySet()) {
				if (entry.getValue() instanceof MetadataEnvironmentModifier) {
					final ILogArgumentRewriter logArgumentRewriter = computeLogArgumentRewriter((MetadataEnvironmentModifier) entry.getValue());
					if (logArgumentRewriter != null) childLogArgumentRewriterMap.put(entry.getKey(), logArgumentRewriter);
				}
			}

			final String base = toString(modified.getBase(), prefix + "\t", childLogArgumentRewriterMap, context);
			final boolean multiLineBase = base.contains("\n");

			final Map<String, String> modifierMap = new LinkedHashMap<>();
			for (Map.Entry<String, IEnvironmentModifier> entry : modified.getModifiers().entrySet()) {
				final String string = toString(entry.getValue(), childLogArgumentRewriterMap.get(entry.getKey()), context);
				if (string != null) modifierMap.put(entry.getKey(), string);
			}
			final int modifierCount = modifierMap.size();

			if (multiLineBase || modifierCount > 1) {
				retVal.append(prefix).append("modified:").append(multiLineBase ? '\n' : ' ').append(multiLineBase ? base : base.strip()).append('\n');
				retVal.append(prefix).append("modifiers:\n");
				for (Map.Entry<String, String> entry : modifierMap.entrySet()) {
					retVal.append(prefix).append('\t').append(entry.getKey()).append(" <- ").append(entry.getValue()).append('\n');
				}
			} else if (modifierCount < 1) retVal.append(base.strip());
			else {
				final Entry<String, String> entry = HCollection.getOne(modifierMap.entrySet());
				retVal.append(prefix).append("modified ").append(base.strip()).append(" with ").append(entry.getKey()).append(" <- ").append(entry.getValue());
			}
		} else if (environment instanceof SystemEnvironment) retVal.append(prefix).append("system environment\n");
		else throw new NotYetImplementedError("Environments of type " + environment.getClass() + " are not yet supported!");
		return retVal.toString().stripTrailing();
	}

	protected static String toString(IEnvironmentModifier modifier, ILogArgumentRewriter logArgumentRewriter, Map<String, Object> context) {
		if (modifier == null) return "null";
		if (modifier instanceof MetadataEnvironmentModifier) {
			final MetadataEnvironmentModifier cast = (MetadataEnvironmentModifier) modifier;
			final ILogArgumentRewriter actual = logArgumentRewriter == null ? NopLogArgumentRewriter.create() : logArgumentRewriter;
			return actual.rewrite(toString(cast.getModifier(), logArgumentRewriter, context), context);
		}
		return modifier.toString();
	}

	protected static String toString(List<? extends CommandArgument<? extends MetaCommandArgument>> arguments, Map<String, Object> context) {
		Objects.requireNonNull(arguments);
		final StringBuilder retVal = new StringBuilder();
		for (int i = 0; i < arguments.size(); i++) {
			if (i > 0) retVal.append(' ');

			final ILogArgumentRewriter logArgumentRewriter = computeLogArgumentRewriter(arguments.get(i).getValue().getMeta());
			final String rewritten = logArgumentRewriter.rewrite(arguments.get(i).getString(), context);
			if (rewritten != null) retVal.append(rewritten);
		}
		return retVal.toString();
	}

	protected final IConsumer1<String> log;

	protected final IFunction1<CommandInvocation<MetaCommandArgument, IRedirect, IRedirect>, IProcess> runner;

	protected final Map<String, Object> context;

	public LoggingRunner(IConsumer1<String> log, IFunction1<CommandInvocation<MetaCommandArgument, IRedirect, IRedirect>, IProcess> runner) {
		this(log, runner, null);
	}

	@Override
	public IProcess apply(CommandInvocation<MetaCommandArgument, IRedirect, IRedirect> commandInvocation) {
		final IConsumer1<String> log = getLog();
		log.accept("Running: " + toString(commandInvocation.getArgumentsAsArguments(), getContext()));
		if (commandInvocation.getWorking() != null) log.accept("\tin " + commandInvocation.getWorking());
		final IEnvironment environment = commandInvocation.getEnvironment();
		if ((environment != null) && !(commandInvocation.getEnvironment() instanceof SystemEnvironment)) {
			final String string = toString(environment, "\t\t", HMap.empty(), getContext());
			if (string.contains("\n")) {
				log.accept("\tenvironment:");
				for (String line : string.split("\n")) {
					log.accept(line);
				}
			} else log.accept("\tenvironment: " + HString.stripPrefix(string, "\t\t"));;
		}
		return getRunner().apply(commandInvocation);
	}
}