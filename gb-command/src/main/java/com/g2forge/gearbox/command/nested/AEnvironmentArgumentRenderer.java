package com.g2forge.gearbox.command.nested;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.g2forge.alexandria.command.invocation.environment.EmptyEnvironment;
import com.g2forge.alexandria.command.invocation.environment.IEnvironment;
import com.g2forge.alexandria.command.invocation.environment.MapEnvironment;
import com.g2forge.alexandria.command.invocation.environment.SystemEnvironment;
import com.g2forge.alexandria.command.invocation.environment.modified.ModifiedEnvironment;
import com.g2forge.alexandria.java.core.error.NotYetImplementedError;
import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.gearbox.command.converter.dumb.IArgumentRenderer;

public abstract class AEnvironmentArgumentRenderer implements IArgumentRenderer<Map<? extends String, ? extends String>> {
	public static IEnvironment envSystemToEmpty(IEnvironment environment) {
		if (environment instanceof SystemEnvironment) return EmptyEnvironment.create();
		if (environment instanceof ModifiedEnvironment) {
			final ModifiedEnvironment modifiedEnvironment = (ModifiedEnvironment) environment;
			final IEnvironment newBase = envSystemToEmpty(modifiedEnvironment.getBase());
			return (newBase != modifiedEnvironment.getBase()) ? modifiedEnvironment.toBuilder().base(newBase).build() : environment;
		}
		if (environment instanceof EmptyEnvironment) return environment;
		if (environment instanceof MapEnvironment) return environment;
		throw new NotYetImplementedError(String.format("Environments of type %1$s are not yet supported!", environment.getClass().getSimpleName()));
	}

	public static List<String> toArguments(final String envArgumentName, final Map<? extends String, ? extends String> environment) {
		final List<String> retVal = new ArrayList<>();
		if (environment != null) for (Map.Entry<? extends String, ? extends String> variable : environment.entrySet()) {
			retVal.add(envArgumentName);
			final StringBuilder variableString = new StringBuilder().append(variable.getKey());
			if ((variable.getValue() != null) && (variable.getValue().length() > 0)) variableString.append('=').append(variable.getValue());
			retVal.add(variableString.toString());
		}
		return retVal;
	}

	protected abstract String getEnvArgumentName();

	public IArgumentModifier insertEnvironmentArguments() {
		return (commandInvocation, builder) -> {
			final List<String> arguments = new ArrayList<>();
			arguments.addAll(builder.build().getArguments());
			if (commandInvocation.getEnvironment() != null) {
				final IEnvironment commandEnvironment = AEnvironmentArgumentRenderer.envSystemToEmpty(commandInvocation.getEnvironment());
				arguments.addAll(arguments.size() - 1, toArguments(commandEnvironment.toMap()));
			}
			arguments.addAll(commandInvocation.getArguments());
			builder.clearArguments();
			builder.arguments(arguments);
		};

	}

	@Override
	public List<String> render(IMethodArgument<Map<? extends String, ? extends String>> argument) {
		return toArguments(argument.get());
	}

	public List<String> toArguments(final Map<? extends String, ? extends String> environment) {
		return toArguments(getEnvArgumentName(), environment);
	}
}