package com.g2forge.gearbox.command.converter.argumentrenderer;

import java.util.List;
import java.util.Objects;

import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.gearbox.command.converter.dumb.HDumbCommandConverter;

public class ToStringArgumentRenderer extends ASimpleArgumentRenderer<Object> {
	@Override
	protected List<String> renderSimple(IMethodArgument<Object> argument) {
		return HDumbCommandConverter.computeString(argument, Objects.toString(argument.get()));
	}
}
