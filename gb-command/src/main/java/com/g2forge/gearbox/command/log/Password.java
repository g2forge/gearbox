package com.g2forge.gearbox.command.log;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A shorthand annotation for {@link Log.Mode#REPLACE}. If both this and {@link Log} are present on a parameter {@link Log} takes precedence.
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Password {}
