package com.g2forge.gearbox.command.converter.dumb;

import java.util.List;
import java.util.stream.Collectors;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.gearbox.command.converter.IMethodArgument;

public class CSVArgumentRenderer implements IArgumentRenderer<List<String>> {
	@Override
	public List<String> render(IMethodArgument<List<String>> argument) {
		final String string = argument.get() == null ? null : argument.get().stream().collect(Collectors.joining(","));
		if (string == null || string.isEmpty()) return HCollection.emptyList();
		return HDumbCommandConverter.computeString(argument, string);
	}
}