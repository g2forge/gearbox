package com.g2forge.gearbox.command.v1.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import com.g2forge.gearbox.command.v1.control.IArgument;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class Argument implements IArgument<Object> {
	protected final Object value;

	protected final Parameter parameter;

	@Override
	public Object get() {
		return value;
	}

	@Override
	public Annotation[] getAnnotations() {
		return parameter.getAnnotations();
	}

	@Override
	public Type getGenericType() {
		return parameter.getParameterizedType();
	}

	@Override
	public String getName() {
		return parameter.getName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<Object> getType() {
		return (Class<Object>) parameter.getType();
	}
}