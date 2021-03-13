package com.g2forge.gearbox.command.converter.dumb;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this argument should be used as part of the path for the child process.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface EnvPath {
	public enum Usage {
		AddFirst,
		Replace,
		AddLast;
	}

	public Usage usage() default Usage.AddFirst;
}
