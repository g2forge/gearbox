package com.g2forge.gearbox.command.converter.dumb;

import java.util.List;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.gearbox.command.converter.IMethodArgument;

public class HDumbCommandConverter {
	public static List<String> computeString(IMethodArgument<?> argument, String value) {
		final Named named = argument.getMetadata().get(Named.class);
		if (named == null) return HCollection.asList(value);
		if (!named.joined()) return HCollection.asList(named.value(), value);
		return HCollection.asList(named.value() + value);
	}
}
