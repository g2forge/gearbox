package com.g2forge.gearbox.command.v1.control;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
	public String[] value();

	public Class<? extends IExplicitResultHandler> handler() default IExplicitResultHandler.class;
}
