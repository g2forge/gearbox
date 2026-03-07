package com.g2forge.gearbox.command.nested;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.g2forge.alexandria.command.invocation.environment.EmptyEnvironment;
import com.g2forge.alexandria.command.invocation.environment.EnvironmentHandler;
import com.g2forge.alexandria.command.invocation.environment.IEnvironment;
import com.g2forge.alexandria.command.invocation.environment.MapEnvironment;
import com.g2forge.alexandria.command.invocation.environment.SystemEnvironment;
import com.g2forge.alexandria.command.invocation.environment.modified.IEnvironmentModifier;
import com.g2forge.alexandria.command.invocation.environment.modified.ModifiedEnvironment;
import com.g2forge.alexandria.java.adt.tuple.implementations.Tuple2G_I;
import com.g2forge.alexandria.java.adt.tuple.implementations.Tuple2G_O;
import com.g2forge.alexandria.java.core.error.NotYetImplementedError;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.helpers.HCollector;
import com.g2forge.alexandria.java.core.helpers.HMap;
import com.g2forge.alexandria.java.function.IFunction3;
import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.gearbox.command.converter.argumentrenderer.IArgumentRenderer;
import com.g2forge.gearbox.command.process.MetaCommandArgument;
import com.g2forge.habitat.metadata.value.subject.ISubject;

public abstract class AEnvironmentArgumentRenderer implements IArgumentRenderer<Map<? extends String, ? extends String>> {
	public static List<MetaCommandArgument> toArguments(final IFunction3<? super ISubject, ? super String, ? super String, ? extends List<MetaCommandArgument>> toEnvArguments, final Map<? extends String, ? extends MetaCommandArgument> environment) {
		final List<MetaCommandArgument> retVal = new ArrayList<>();
		if (environment != null) for (Map.Entry<? extends String, ? extends MetaCommandArgument> variable : environment.entrySet()) {
			retVal.addAll(toEnvArguments.apply(variable.getValue().getMeta(), variable.getKey(), variable.getValue().getValue()));
		}
		return retVal;
	}

	@EnvironmentHandler
	public static IEnvironment toEmptySystemEnvironment(IEnvironment environment) {
		if (environment instanceof SystemEnvironment) return EmptyEnvironment.create();
		if (environment instanceof ModifiedEnvironment) {
			final ModifiedEnvironment modifiedEnvironment = (ModifiedEnvironment) environment;
			final IEnvironment newBase = toEmptySystemEnvironment(modifiedEnvironment.getBase());
			return (newBase != modifiedEnvironment.getBase()) ? modifiedEnvironment.toBuilder().base(newBase).build() : environment;
		}
		if (environment instanceof EmptyEnvironment) return environment;
		if (environment instanceof MapEnvironment) return environment;
		throw new NotYetImplementedError(String.format("Environments of type %1$s are not yet supported!", environment.getClass().getSimpleName()));
	}

	@EnvironmentHandler
	public static Map<String, MetaCommandArgument> toEnvironmentArgumentMap(IEnvironment environment) {
		if (environment instanceof ModifiedEnvironment) {
			final ModifiedEnvironment modifiedEnvironment = (ModifiedEnvironment) environment;
			final Map<String, MetaCommandArgument> retVal = toEnvironmentArgumentMap(modifiedEnvironment.getBase());
			for (Map.Entry<String, IEnvironmentModifier> entry : modifiedEnvironment.getModifiers().entrySet()) {
				final String variable = entry.getKey();

				final MetaCommandArgument baseMCA = retVal.get(variable);
				final String baseValue = baseMCA == null ? null : baseMCA.getValue();

				final String modifiedValue = entry.getValue().modify(baseValue);
				if (modifiedValue == null) retVal.remove(variable);
				else {
					final ISubject modifiedSubject;
					if (baseMCA != null) modifiedSubject = baseMCA.getMeta();
					else modifiedSubject = null;

					final MetaCommandArgument modifiedMCA = new MetaCommandArgument(modifiedValue, modifiedSubject);
					if (!Objects.equals(baseMCA, modifiedMCA)) retVal.put(variable, modifiedMCA);
				}
			}
			return HMap.unmodifiableMap(retVal);
		}
		if ((environment instanceof MapEnvironment) || (environment instanceof SystemEnvironment) || (environment instanceof EmptyEnvironment)) return environment.toMap().entrySet().stream().map(e -> new Tuple2G_I<>(e.getKey(), new MetaCommandArgument(e.getValue(), null))).collect(HCollector.toMapTuples());
		throw new NotYetImplementedError(String.format("Environments of type %1$s are not yet supported!", environment.getClass().getSimpleName()));
	}

	public IArgumentModifier insertEnvironmentArguments() {
		return (commandInvocation, builder) -> {
			final List<MetaCommandArgument> arguments = new ArrayList<>();
			arguments.addAll(builder.build().getArguments());
			if (commandInvocation.getEnvironment() != null) {
				final IEnvironment commandEnvironment = AEnvironmentArgumentRenderer.toEmptySystemEnvironment(commandInvocation.getEnvironment());
				arguments.addAll(arguments.size() - 1, toArguments(toEnvironmentArgumentMap(commandEnvironment)));
			}
			arguments.addAll(commandInvocation.getArguments());
			builder.clearArguments();
			builder.arguments(arguments);
		};
	}

	@Override
	public List<MetaCommandArgument> render(IMethodArgument<Map<? extends String, ? extends String>> argument) {
		final Map<? extends String, ? extends String> stringMap = argument.get();
		final Map<? extends String, MetaCommandArgument> mcaMap;
		if (stringMap == null) mcaMap = null;
		else mcaMap = stringMap.entrySet().stream().map(e -> new Tuple2G_O<>(e.getKey(), new MetaCommandArgument(e.getValue(), argument.getMetadata()))).collect(HCollector.toMapTuples());
		return toArguments(mcaMap);
	}

	public List<MetaCommandArgument> toArguments(final Map<? extends String, ? extends MetaCommandArgument> environment) {
		return toArguments(this::toEnvArguments, environment);
	}

	/**
	 * Convert a single environment variable specification into the relevant command line arguments. By default this will create docker/podman style arguments:
	 * {@code --env VAR=value}.
	 * 
	 * @param meta The metadata to associated with the value. By default this is only attached to the arguments that contain non-constant values.
	 * @param name The name of the environment variable.
	 * @param value The value of the environment variable.
	 * @return A list of command line arguments.
	 */
	protected List<MetaCommandArgument> toEnvArguments(ISubject meta, String name, String value) {
		if (value == null) return HCollection.emptyList();
		// Standard docker/podman style arguments
		return HCollection.asList(new MetaCommandArgument("--env", null), new MetaCommandArgument(name + "=" + value, meta));
	}
}