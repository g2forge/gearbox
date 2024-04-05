package com.g2forge.gearbox.argparse;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ListIterator;

import com.g2forge.alexandria.java.core.error.UnreachableCodeError;
import com.g2forge.alexandria.java.fluent.optional.IOptional;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER })
public @interface Parameter {
	public static final class DefaultParser implements IParameterParser {
		@Override
		public IOptional<Object> getDefault(IParameterInfo parameter) {
			throw new UnreachableCodeError();
		}

		@Override
		public Object parse(IParameterInfo parameter, ListIterator<String> argumentIterator) {
			throw new UnreachableCodeError();
		}
	}

	public Class<? extends IParameterParser> parser() default DefaultParser.class;

	public String value();
}
