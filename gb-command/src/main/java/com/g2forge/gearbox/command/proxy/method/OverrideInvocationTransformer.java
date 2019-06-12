package com.g2forge.gearbox.command.proxy.method;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OverrideInvocationTransformer {
	public Class<? extends IFunction1<MethodInvocation, ProcessInvocation<?>>> value();
}