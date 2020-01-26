package com.g2forge.gearbox.command.proxy.transformers;

import com.g2forge.gearbox.command.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;
import com.g2forge.habitat.metadata.Metadata;
import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class MethodToCommandInvocationTransformer implements IInvocationTransformer {
	protected final ICommandConverterR_ renderer;

	@Override
	public ProcessInvocation<?> apply(MethodInvocation methodInvocation) {
		final ICommandConverterR_ loaded = getRenderer(methodInvocation);
		final ICommandConverterR_ renderer = (loaded == null) ? getRenderer() : loaded;
		return renderer.apply(ProcessInvocation.builder().build(), methodInvocation);
	}

	protected ICommandConverterR_ getRenderer(MethodInvocation methodInvocation) {
		final ISubject subject = Metadata.getStandard().of(methodInvocation.getMethod());
		return subject.get(ICommandConverterR_.class);
	}
}
