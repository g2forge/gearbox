package com.g2forge.gearbox.functional.control;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.gearbox.functional.runner.redirect.IRedirect;

public interface IArgumentContext {
	public IArgument<Object> getArgument();

	public CommandInvocation.CommandInvocationBuilder<IRedirect, IRedirect> getCommandInvocation();
}
