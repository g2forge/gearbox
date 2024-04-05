package com.g2forge.gearbox.argparse;

import java.util.ListIterator;

import com.g2forge.alexandria.java.fluent.optional.IOptional;

public interface IParameterParser {
	/**
	 * Get the default value for this parameter, in case the user does not specify it on the command line.
	 * 
	 * @param parameter The parameter.
	 * @return An optional default value. If the optional is empty, no value will be set.
	 */
	public IOptional<Object> getDefault(IParameterInfo parameter);

	/**
	 * Parse one or more command line arguments to fill in the specified parameter.
	 * 
	 * @param parameter The parameter to parse
	 * @param argumentIterator The command line argument iterator.
	 * @return The parsed parameter value
	 */
	public Object parse(IParameterInfo parameter, ListIterator<String> argumentIterator);
}