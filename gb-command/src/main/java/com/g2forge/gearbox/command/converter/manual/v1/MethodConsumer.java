package com.g2forge.gearbox.command.converter.manual.v1;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.gearbox.command.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(MethodConsumers.class)
public @interface MethodConsumer {
	public boolean pre() default true;

	public Class<? extends IConsumer2<ProcessInvocation.ProcessInvocationBuilder<Object>, MethodInvocation>> value();
}
