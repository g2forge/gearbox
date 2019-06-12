package com.g2forge.gearbox.command.v2.converter.dumb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a constant argument, to be added to the command line after the argument this annotation appears on.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Constant {
	public String[] value();
}
