package com.g2forge.gearbox.functional.control;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a constant argument, to be added to the command line after the argument this annotation appears on, or before all arguments if this is on the
 * method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.METHOD })
public @interface Constant {
	public String[] value();
}
