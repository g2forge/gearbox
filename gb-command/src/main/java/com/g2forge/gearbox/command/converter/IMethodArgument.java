package com.g2forge.gearbox.command.converter;

import java.lang.reflect.Type;

import com.g2forge.alexandria.metadata.IMetadata;

public interface IMethodArgument<T> {
	public T get();

	public Type getGenericType();

	public IMetadata getMetadata();

	public String getName();

	public Class<T> getType();
}