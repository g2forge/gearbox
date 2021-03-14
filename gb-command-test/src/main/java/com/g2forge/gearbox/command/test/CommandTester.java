package com.g2forge.gearbox.command.test;

import java.util.List;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.IRunner;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.proxy.CommandProxyFactory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommandTester<Command> {
	@AllArgsConstructor
	@Getter
	protected static class CommandException extends Error {
		private static final long serialVersionUID = -8389195474206822767L;

		protected final CommandInvocation<IRedirect, IRedirect> commandInvocation;
	}

	protected class ThrowingRunner implements IRunner {
		@Override
		public IProcess apply(CommandInvocation<IRedirect, IRedirect> commandInvocation) {
			throw new CommandException(commandInvocation);
		}
	}

	@Getter(AccessLevel.PROTECTED)
	protected final Command command;

	public CommandTester(ICommandConverterR_ converter, Class<Command> commandClass) {
		this.command = new CommandProxyFactory(converter, new ThrowingRunner()).apply(commandClass);
	}

	public void assertArguments(IFunction1<? super Command, ?> actual, String... expected) {
		assertArguments(HCollection.asList(expected), actual);
	}

	public void assertArguments(List<String> expected, IFunction1<? super Command, ?> actual) {
		try {
			actual.apply(getCommand());
		} catch (CommandException exception) {
			HAssert.assertArrayEquals(expected.toArray(new String[0]), exception.getCommandInvocation().getArguments().toArray(new String[0]));
			return;
		}
		HAssert.fail(String.format("Command invocation did not throw the expected exception!"));
	}
}
