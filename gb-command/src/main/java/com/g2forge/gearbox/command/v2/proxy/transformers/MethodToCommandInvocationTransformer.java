package com.g2forge.gearbox.command.v2.proxy.transformers;

import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.gearbox.command.v2.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;

public class MethodToCommandInvocationTransformer implements IInvocationTransformer {
	@Override
	public ProcessInvocation<?> apply(MethodInvocation methodInvocation) {
		final ICommandConverterR_ renderer = getRenderer(methodInvocation);
		return renderer.apply(ProcessInvocation.builder().build(), methodInvocation);
	}

	protected ICommandConverterR_ getRenderer(MethodInvocation methodInvocation) {
		final IMetadata metadata = IMetadata.of(methodInvocation.getMethod());
		return metadata.getMetadata(ICommandConverterR_.class);
	}
}
