package com.g2forge.gearbox.command.converter;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import com.g2forge.habitat.metadata.Metadata;
import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class MethodArgument implements IMethodArgument<Object> {
	protected final Object value;

	protected final Parameter parameter;

	@Override
	public Object get() {
		return getValue();
	}

	@Override
	public Type getGenericType() {
		return getParameter().getParameterizedType();
	}

	@Override
	public ISubject getMetadata() {
		return Metadata.getStandard().of(getParameter(), getValue());
	}

	@Override
	public String getName() {
		return getParameter().getName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<Object> getType() {
		return (Class<Object>) getParameter().getType();
	}
}