package com.g2forge.gearbox.command.v2.proxy.transformers;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.proxy.transformers.IInvocationTransformer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomInvocationTransformer implements IInvocationTransformer {
	protected final CommandInvocation<IRedirect, IRedirect> commandInvocation;

	protected final Object retVal;

	@Override
	public ProcessInvocation<?> apply(MethodInvocation methodInvocation) {
		return new ProcessInvocation<>(commandInvocation, process -> retVal);
	}
}