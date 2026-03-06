package com.g2forge.gearbox.argparse;

import java.util.List;

import com.g2forge.alexandria.command.invocation.CommandInvocation;

public interface IArgumentParser<T> {
	public <A> T parse(CommandInvocation<A, ?, ?> invocation);

	public default T parse(List<String> arguments) {
		return parse(CommandInvocation.createFromArgumentsOnly(arguments));
	}

	public default T parse(String... arguments) {
		return parse(CommandInvocation.createFromArgumentsOnly(arguments));
	}
}
