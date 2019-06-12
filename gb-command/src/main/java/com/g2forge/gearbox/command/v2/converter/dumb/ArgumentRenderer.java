package com.g2forge.gearbox.command.v2.converter.dumb;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface ArgumentRenderer {
	public Class<? extends IArgumentRenderer<?>> value();
}
