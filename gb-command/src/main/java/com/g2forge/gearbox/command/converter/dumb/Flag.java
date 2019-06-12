package com.g2forge.gearbox.command.converter.dumb;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a boolean argument, which when <code>true</code> will result in the {@link #value()} being added to the command line.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface Flag {
	public String value();
}
