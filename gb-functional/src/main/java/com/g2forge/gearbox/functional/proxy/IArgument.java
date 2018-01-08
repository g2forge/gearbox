package com.g2forge.gearbox.functional.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.stream.Stream;

interface IArgument<T> {
	public T get();

	public default <A extends Annotation> A getAnnotation(Class<A> type) {
		return Stream.of(getAnnotations()).filter(type::isInstance).map(type::cast).findAny().orElseGet(() -> null);
	}

	public Annotation[] getAnnotations();

	public Type getGenericType();

	public String getName();

	public Class<T> getType();
}