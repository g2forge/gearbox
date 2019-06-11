package com.g2forge.gearbox.command.v2.proxy.transformers;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import com.g2forge.alexandria.java.core.error.RuntimeReflectionException;
import com.g2forge.alexandria.java.core.helpers.HStream;
import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.gearbox.command.v2.converter.CommandConverter;
import com.g2forge.gearbox.command.v2.converter.CommandConverters;
import com.g2forge.gearbox.command.v2.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.v2.converter.ICommandConverter__;
import com.g2forge.gearbox.command.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;

public class MethodToCommandInvocationTransformer implements IInvocationTransformer {
	@Override
	public ProcessInvocation<?> apply(MethodInvocation methodInvocation) {
		final ICommandConverterR_ renderer = getRenderer(methodInvocation);
		return renderer.apply(ProcessInvocation.builder().build(), methodInvocation);
	}

	protected ICommandConverterR_ getRenderer(MethodInvocation methodInvocation) {
		final Method method = methodInvocation.getMethod();
		final CommandConverters commandConvertersMetadata = IMetadata.of(method).getMetadata(CommandConverters.class);
		final Class<? extends ICommandConverter__> rendererType = HStream.findOne(Stream.of(commandConvertersMetadata.value()).map(CommandConverter::value).filter(ICommandConverterR_.class::isAssignableFrom));
		final ICommandConverterR_ renderer;
		try {
			renderer = ((ICommandConverterR_) rendererType.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeReflectionException(e);
		}
		return renderer;
	}
}
