package com.g2forge.gearbox.command.converter.argumentrenderer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.gearbox.command.converter.dumb.HDumbCommandConverter;

public class CSVArgumentRenderer extends ASimpleArgumentRenderer<Object> {
	@Override
	protected List<String> renderSimple(IMethodArgument<Object> argument) {
		final Stream<String> stream;
		final Object value = argument.get();
		if (value == null) return HCollection.emptyList();
		else if (value instanceof String[]) stream = Stream.of((String[]) value);
		else if (value instanceof Collection) {
			@SuppressWarnings("unchecked")
			final Collection<String> includes = (Collection<String>) value;
			stream = includes.stream();
		} else throw new IllegalArgumentException(String.format("%1$s with type %2$s cannot be converted to a stream to use as a CSV command argument", value, value.getClass()));

		final String string = stream.collect(Collectors.joining(","));
		if (string.isEmpty()) return HCollection.emptyList();
		return HDumbCommandConverter.computeString(argument, string);
	}
}