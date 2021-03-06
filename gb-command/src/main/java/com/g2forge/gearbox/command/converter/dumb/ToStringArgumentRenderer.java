package com.g2forge.gearbox.command.converter.dumb;

import java.util.List;
import java.util.Objects;

import com.g2forge.gearbox.command.converter.IMethodArgument;

public class ToStringArgumentRenderer implements IArgumentRenderer<Object> {
	@Override
	public List<String> render(IMethodArgument<Object> argument) {
		return HDumbCommandConverter.computeString(argument, Objects.toString(argument.get()));
	}
}
