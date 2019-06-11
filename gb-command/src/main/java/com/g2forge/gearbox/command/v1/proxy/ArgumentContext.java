package com.g2forge.gearbox.command.v1.proxy;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.gearbox.command.v1.control.IArgument;
import com.g2forge.gearbox.command.v1.control.IArgumentContext;
import com.g2forge.gearbox.command.v1.runner.redirect.IRedirect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
class ArgumentContext implements IArgumentContext {
	protected final CommandInvocation.CommandInvocationBuilder<IRedirect, IRedirect> commandInvocation;

	protected final IArgument<Object> argument;
}