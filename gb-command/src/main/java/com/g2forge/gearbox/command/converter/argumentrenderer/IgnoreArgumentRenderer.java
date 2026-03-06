package com.g2forge.gearbox.command.converter.argumentrenderer;

import java.util.Collections;
import java.util.List;

import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.gearbox.command.process.MetaCommandArgument;

/**
 * A renderer which ignores a method argument when constructing a command line. You can use this as an argument to {@link ArgumentRenderer}.
 */
public class IgnoreArgumentRenderer implements IArgumentRenderer<Object> {
	@Override
	public List<MetaCommandArgument> render(IMethodArgument<Object> argument) {
		return Collections.emptyList();
	}
}