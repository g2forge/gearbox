package com.g2forge.gearbox.command.converter.dumb;

import java.util.Collections;
import java.util.List;

import com.g2forge.gearbox.command.converter.IMethodArgument;

/**
 * A renderer which ignores a method argument when constructing a command line. You can use this as an argument to {@link ArgumentRenderer}.
 */
public class IgnoreArgumentRenderer implements IArgumentRenderer<Object> {
	@Override
	public List<String> render(IMethodArgument<Object> argument) {
		return Collections.emptyList();
	}
}