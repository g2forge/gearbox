package com.g2forge.gearbox.command.converter.argumentrenderer;

import java.util.List;

import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.gearbox.command.process.MetaCommandArgument;

public abstract class ASimpleArgumentRenderer<T> implements IArgumentRenderer<T> {
	protected abstract List<String> renderSimple(IMethodArgument<T> argument);

	@Override
	public List<MetaCommandArgument> render(IMethodArgument<T> argument) {
		final List<String> simple = renderSimple(argument);
		return simple.stream().map(s -> new MetaCommandArgument(s, argument.getMetadata())).toList();
	}
}
