package com.g2forge.gearbox.command.log;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.command.invocation.environment.IEnvironment;
import com.g2forge.alexandria.command.invocation.environment.MapEnvironment;
import com.g2forge.alexandria.command.invocation.environment.SystemEnvironment;
import com.g2forge.alexandria.command.invocation.environment.modified.IEnvironmentModifier;
import com.g2forge.alexandria.command.invocation.environment.modified.ModifiedEnvironment;
import com.g2forge.alexandria.java.core.enums.EnumException;
import com.g2forge.alexandria.java.core.error.NotYetImplementedError;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.helpers.HMap;
import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.java.function.IFunction2;
import com.g2forge.alexandria.java.text.HString;
import com.g2forge.gearbox.command.converter.MetadataEnvironmentModifier;
import com.g2forge.gearbox.command.process.CommandMetadata;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.IRunner;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class LoggingRunner implements IRunner {
	protected static final Log DEFAULT_LOG = new Log() {
		@Override
		public Class<? extends Annotation> annotationType() {
			return getClass();
		}

		@Override
		public String replacement() {
			return Log.DEFAULT_REPLACEMENT;
		}

		@Override
		public Mode value() {
			return Mode.NORMAL;
		}
	};

	protected static final Log PASSWORD_LOG = new Log() {
		@Override
		public Class<? extends Annotation> annotationType() {
			return getClass();
		}

		@Override
		public String replacement() {
			return Log.DEFAULT_REPLACEMENT;
		}

		@Override
		public Mode value() {
			return Mode.REPLACE;
		}
	};

	protected static Log computeLog(final MetadataEnvironmentModifier mem) {
		MetadataEnvironmentModifier current = mem;
		final List<Log> logs = new ArrayList<>();
		while (true) {
			final Log log = getLogForSubject(current.getSubject());
			if (log != null) logs.add(log);

			if (current.getModifier() instanceof MetadataEnvironmentModifier) current = (MetadataEnvironmentModifier) current.getModifier();
			else break;
		}

		return merge(logs);
	}

	protected static Log getLogForSubject(final ISubject subject) {
		if (subject != null) {
			final Log log = subject.get(Log.class);
			if (log != null) return log;
			else return subject.isPresent(Password.class) ? LoggingRunner.PASSWORD_LOG : LoggingRunner.DEFAULT_LOG;
		} else return LoggingRunner.DEFAULT_LOG;
	}

	protected static Log merge(Iterable<? extends Log> logs) {
		Log current = null;
		for (Log log : logs) {
			if (log == null) continue;
			else if (current == null) current = log;
			else {
				if (log.value().compareTo(current.value()) > 0) current = log;
			}
		}
		return current;
	}

	protected static String toString(IEnvironment environment, String prefix, Map<String, Log> logMap) {
		final StringBuilder retVal = new StringBuilder();
		if (environment instanceof MapEnvironment) {
			for (Map.Entry<String, String> entry : ((MapEnvironment) environment).getVariables().entrySet()) {
				final Log log = logMap.getOrDefault(entry.getKey(), DEFAULT_LOG);
				switch (log.value()) {
					case NORMAL:
						retVal.append(prefix).append(entry.getKey()).append('=').append(entry.getValue()).append("\n");
						break;
					case REPLACE:
						retVal.append(prefix).append(entry.getKey()).append('=').append(log.replacement()).append("\n");
						break;
					case NOTHING:
						break;
					default:
						throw new EnumException(Log.Mode.class, log.value());
				}
			}
		} else if (environment instanceof ModifiedEnvironment) {
			final ModifiedEnvironment modified = ((ModifiedEnvironment) environment);

			final Map<String, Log> childLogMap = new LinkedHashMap<>(logMap);
			for (Map.Entry<String, IEnvironmentModifier> entry : modified.getModifiers().entrySet()) {
				if (entry.getValue() instanceof MetadataEnvironmentModifier) {
					final Log log = computeLog((MetadataEnvironmentModifier) entry.getValue());
					if (log != null) childLogMap.put(entry.getKey(), log);
				}
			}

			final String base = toString(modified.getBase(), prefix + "\t", childLogMap);
			final boolean multiLineBase = base.contains("\n");

			final Map<String, String> modifierMap = new LinkedHashMap<>();
			for (Map.Entry<String, IEnvironmentModifier> entry : modified.getModifiers().entrySet()) {
				final String string = toString(entry.getValue(), childLogMap.get(entry.getKey()));
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
		} else if (environment instanceof SystemEnvironment) {
			retVal.append(prefix).append("system environment\n");
		} else throw new NotYetImplementedError("Environments of type " + environment.getClass() + " are not yet supported!");

		return retVal.toString().stripTrailing();
	}

	protected static String toString(IEnvironmentModifier modifier, Log log) {
		if (modifier == null) return "null";
		if (modifier instanceof MetadataEnvironmentModifier) {
			final MetadataEnvironmentModifier cast = (MetadataEnvironmentModifier) modifier;
			if (log == null) return toString(cast.getModifier(), log);
			switch (log.value()) {
				case NORMAL:
					return toString(cast.getModifier(), log);
				case REPLACE:
					return log.replacement();
				case NOTHING:
					return null;
				default:
					throw new EnumException(Log.Mode.class, log.value());
			}
		}
		return modifier.toString();
	}

	protected static String toString(List<String> arguments, List<ISubject> metadata) {
		Objects.requireNonNull(arguments);
		if ((metadata != null) && (arguments.size() != metadata.size())) throw new IllegalArgumentException(String.format("Number of arguments (%1$d) does not match number of metadata (%2$d)", arguments.size(), metadata.size()));
		final StringBuilder retVal = new StringBuilder();
		for (int i = 0; i < arguments.size(); i++) {
			if (i > 0) retVal.append(' ');

			final Log log = (metadata != null) ? getLogForSubject(metadata.get(i)) : DEFAULT_LOG;
			switch (log.value()) {
				case NORMAL:
					retVal.append(arguments.get(i));
					break;
				case REPLACE:
					retVal.append(log.replacement());
					break;
				case NOTHING:
					break;
				default:
					throw new EnumException(Log.Mode.class, log.value());
			}
		}
		return retVal.toString();
	}

	protected final IConsumer1<String> log;

	protected final IFunction2<CommandInvocation<IRedirect, IRedirect>, CommandMetadata, IProcess> runner;

	@Override
	public IProcess apply(CommandInvocation<IRedirect, IRedirect> commandInvocation, CommandMetadata commandMetadata) {
		final IConsumer1<String> log = getLog();
		log.accept("Running: " + toString(commandInvocation.getArguments(), commandMetadata.getArguments()));
		if (commandInvocation.getWorking() != null) log.accept("\tin " + commandInvocation.getWorking());
		final IEnvironment environment = commandInvocation.getEnvironment();
		if ((environment != null) && !(commandInvocation.getEnvironment() instanceof SystemEnvironment)) {
			final String string = toString(environment, "\t\t", HMap.empty());
			if (string.contains("\n")) {
				log.accept("\tenvironment:");
				for (String line : string.split("\n")) {
					log.accept(line);
				}
			} else log.accept("\tenvironment: " + HString.stripPrefix(string, "\t\t"));;
		}
		return getRunner().apply(commandInvocation, commandMetadata);
	}
}