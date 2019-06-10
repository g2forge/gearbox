package com.g2forge.gearbox.functional.proxy;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.gearbox.functional.control.IArgument;
import com.g2forge.gearbox.functional.control.IArgumentContext;
import com.g2forge.gearbox.functional.runner.redirect.IRedirect;

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