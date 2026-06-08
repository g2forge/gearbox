package com.g2forge.gearbox.command.proxy.result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.alexandria.java.function.ISupplier;
import com.g2forge.alexandria.java.io.HTextIO;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.habitat.metadata.value.subject.ISubject;

public class StringResultSupplier implements IResultSupplier<String>, ISingleton {
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Whitespace {
		public String newlineReplacement() default "\n";

		public boolean stripLeading() default false;

		public boolean stripTrailing() default false;
	}

	protected static final StringResultSupplier INSTANCE = new StringResultSupplier();

	public static IResultSupplier<String> create() {
		return INSTANCE;
	}

	@Override
	public String apply(ISubject subject, IProcess process) {
		try {
			process.assertSuccess();
			final String original = process(() -> HTextIO.readAll(process.getStandardOutput(), true), () -> HTextIO.readAll(process.getStandardError(), true));

			final Whitespace whitespace = subject.get(Whitespace.class);
			if (whitespace == null) return original;

			final String stripped;
			if (whitespace.stripLeading() && whitespace.stripTrailing()) stripped = original.strip();
			else if (whitespace.stripTrailing()) stripped = original.stripTrailing();
			else if (whitespace.stripLeading()) stripped = original.stripLeading();
			else stripped = original;

			return stripped.replaceAll("\\R", whitespace.newlineReplacement());
		} finally {
			process.close();
		}
	}

	protected String process(ISupplier<String> stdout, ISupplier<String> stderr) {
		return stdout.get();
	}
}