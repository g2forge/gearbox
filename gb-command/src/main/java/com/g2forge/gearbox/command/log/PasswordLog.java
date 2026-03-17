package com.g2forge.gearbox.command.log;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PasswordLog {
	public static final String DEFAULT_REPLACEMENT = "***";

	public String value() default DEFAULT_REPLACEMENT;
}
