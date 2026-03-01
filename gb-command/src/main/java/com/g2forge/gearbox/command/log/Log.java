package com.g2forge.gearbox.command.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the command argument should not be logged.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface Log {
	public static final String DEFAULT_REPLACEMENT = "***";

	public enum Mode {
		/** Log the argument as normal */
		NORMAL,

		/** Log the replacement value from {@link Log#replacement()} instead of this argument. This is commonly used for passwords. */
		REPLACE,

		/** Do not log anything. Use this with care, as it can and will fool the user into thinking to no argument was specified. */
		NOTHING;
	}

	public Mode value()

	default Mode.NORMAL;

	public String replacement() default DEFAULT_REPLACEMENT;
}
