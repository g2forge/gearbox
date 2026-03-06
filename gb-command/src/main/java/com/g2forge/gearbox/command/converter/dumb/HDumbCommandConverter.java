package com.g2forge.gearbox.command.converter.dumb;

import java.util.List;

import com.g2forge.alexandria.command.invocation.environment.modified.EnvironmentValue;
import com.g2forge.alexandria.command.invocation.environment.modified.IEnvironmentModifier;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.gearbox.command.converter.MetadataEnvironmentModifier;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter.ArgumentContext;
import com.g2forge.gearbox.command.process.MetaCommandArgument;
import com.g2forge.habitat.metadata.value.subject.ISubject;

public class HDumbCommandConverter {
	public static List<String> computeString(IMethodArgument<?> argument, String value) {
		final Named named = argument.getMetadata().get(Named.class);
		if (named != null) {
			if (!named.joined()) return HCollection.asList(named.value(), value);
			return HCollection.asList(named.value() + value);
		}

		return HCollection.asList(value);
	}

	public static void set(ArgumentContext argumentContext, IMethodArgument<?> argument, String value) {
		//final CommandInvocationBuilder<IRedirect, IRedirect> command = argumentContext.getCommand();
		final ISubject metadata = argument.getMetadata();

		// Handle named arguments
		final Named named = metadata.get(Named.class);
		if (named != null) {
			if (value == null) {
				if (named.skipNull()) return;
				else throw new NullPointerException("Named argument values cannot be null (though they can be the string spelling \"null\"), please check the value for \"" + named.value() + "\"!");
			}
			if (!named.joined()) {
				argumentContext.getCommand().argument(new MetaCommandArgument(named.value(), null));
				argumentContext.getCommand().argument(new MetaCommandArgument(value, argument.getMetadata()));
			} else argumentContext.getCommand().argument(new MetaCommandArgument(named.value() + value, argument.getMetadata()));

			return;
		}

		// Handle environment variables
		final Environment environment = metadata.get(Environment.class);
		if (environment != null) {
			// Null environment values, do nothing
			if (value != null) {
				final IEnvironmentModifier modifier = new MetadataEnvironmentModifier(metadata, new EnvironmentValue(value));
				argumentContext.getEnvironment().modifier(environment.value(), modifier);
			}
			return;
		}

		if (value == null) throw new NullPointerException("Positional argument values cannot be null (though they can be the string spelling \"null\")!");
		argumentContext.getCommand().argument(new MetaCommandArgument(value, argument.getMetadata()));
	}
}
