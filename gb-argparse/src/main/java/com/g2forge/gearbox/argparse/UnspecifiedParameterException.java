package com.g2forge.gearbox.argparse;

public class UnspecifiedParameterException extends IllegalArgumentException {
	private static final long serialVersionUID = 5080026838657436944L;

	public UnspecifiedParameterException(IParameterInfo parameter) {
		super(String.format("Parameter #%1$d (%2$s) was not specified!", parameter.getIndex(), parameter.getName()));
		this.parameter = parameter;
	}

	protected final IParameterInfo parameter;
}
