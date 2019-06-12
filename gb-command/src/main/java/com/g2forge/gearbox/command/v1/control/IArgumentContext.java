package com.g2forge.gearbox.command.v1.control;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.gearbox.command.process.redirect.IRedirect;

public interface IArgumentContext {
	public IArgument<Object> getArgument();

	public CommandInvocation.CommandInvocationBuilder<IRedirect, IRedirect> getCommandInvocation();
}
