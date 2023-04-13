package com.g2forge.gearbox.command.converter;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import com.g2forge.habitat.metadata.Metadata;
import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
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
	public ISubject getMetadata() {
		return Metadata.getStandard().of(parameter, value);
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