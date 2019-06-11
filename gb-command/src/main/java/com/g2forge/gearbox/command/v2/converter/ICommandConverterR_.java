package com.g2forge.gearbox.command.v2.converter;

import com.g2forge.gearbox.command.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;

@FunctionalInterface
public interface ICommandConverterR_ extends ICommandConverter__ {
	public <T> ProcessInvocation<T> apply(ProcessInvocation<T> processInvocation, MethodInvocation methodInvocation);
}
