package com.g2forge.gearbox.command.v2.converter;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import com.g2forge.alexandria.metadata.IMetadata;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MethodArgument implements IMethodArgument<Object> {
	protected final Object value;

	protected final Parameter parameter;

	@Override
	public Object get() {
		return value;
	}

	@Override
	public Type getGenericType() {
		return parameter.getParameterizedType();
	}

	@Override
	public IMetadata getMetadata() {
		return IMetadata.of(parameter, value);
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