package com.g2forge.gearbox.command.converter.dumb;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.g2forge.gearbox.command.proxy.result.IResultSupplier;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface ResultSupplier {
	public Class<? extends IResultSupplier<?>> value();
}
