package com.g2forge.gearbox.argparse;

import com.g2forge.alexandria.command.invocation.CommandArgument;

import lombok.Getter;

@Getter
public class UnparseableArgumentException extends IllegalArgumentException {
	private static final long serialVersionUID = 4965745309281531552L;

	protected final int index;

	protected final CommandArgument<?> argument;

	public UnparseableArgumentException(int index, CommandArgument<?> argument, Throwable cause) {
		super(String.format("Failed to parse argument #%1$d (\"%2$s\")!", index, argument.getString()), cause);
		this.index = index;
		this.argument = argument;
	}
}
