package com.g2forge.gearbox.argparse;

import java.util.ListIterator;

import com.g2forge.alexandria.java.fluent.optional.IOptional;

public interface IParameterParser {
	public IOptional<Object> getDefault(IParameterInfo parameter);

	public Object parse(IParameterInfo parameter, ListIterator<String> argumentIterator);
}