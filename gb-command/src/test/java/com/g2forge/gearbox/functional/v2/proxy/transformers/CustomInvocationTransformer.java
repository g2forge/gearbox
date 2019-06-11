package com.g2forge.gearbox.functional.v2.proxy.transformers;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.gearbox.functional.runner.redirect.IRedirect;
import com.g2forge.gearbox.functional.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.functional.v2.proxy.process.ProcessInvocation;

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