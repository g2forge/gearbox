package com.g2forge.gearbox.command.converter;

import java.lang.reflect.Type;

import com.g2forge.habitat.metadata.value.subject.ISubject;

public interface IMethodArgument<T> {
	public T get();

	public Type getGenericType();

	public ISubject getMetadata();

	public String getName();

	public Class<T> getType();
}