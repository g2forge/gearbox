package com.g2forge.gearbox.argparse;

public class UnparseableArgumentException extends IllegalArgumentException {
	public UnparseableArgumentException(int index, String argument, Throwable cause) {
		super(String.format("Failed to parse argument #%1$d (\"%2$s\")!", index, argument), cause);
		this.index = index;
		this.argument = argument;
	}

	private static final long serialVersionUID = 4965745309281531552L;

	protected final int index;

	protected final String argument;
}
