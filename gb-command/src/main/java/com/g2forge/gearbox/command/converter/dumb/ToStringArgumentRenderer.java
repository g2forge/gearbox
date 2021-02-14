package com.g2forge.gearbox.command.converter.dumb;

import java.util.List;
import java.util.Objects;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.gearbox.command.converter.IMethodArgument;

public class ToStringArgumentRenderer implements IArgumentRenderer<Object> {
	@Override
	public List<String> render(IMethodArgument<Object> argument) {
		return HCollection.asList(Objects.toString(argument.get()));
	}
}
