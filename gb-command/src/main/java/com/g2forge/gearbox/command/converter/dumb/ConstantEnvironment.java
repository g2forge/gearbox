package com.g2forge.gearbox.command.converter.dumb;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a constant environment variable to set for the child process.  You can specify multiple of these through {@link Command#env}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ConstantEnvironment {
	public String variable();

	public String value();
}
