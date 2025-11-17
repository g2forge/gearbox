package com.g2forge.gearbox.command.proxy;

import org.junit.Test;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.command.stdio.StandardIO;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.process.redirect.InheritRedirect;
import com.g2forge.gearbox.command.proxy.method.ITestCommandInterface;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.proxy.process.ReturnProcessInvocationException;
import com.g2forge.gearbox.command.proxy.result.IntegerResultSupplier;

public class TestCommandProxyFactory {
	public static class FactoriedCommandFactory implements ICommandFactory<IFactoriedCommand> {
		public static int OFFSET = 2;

		@Override
		public IFactoriedCommand create(ICommandProxyFactory factory) {
			return new IFactoriedCommand() {
				@Override
				public int method(int argument) {
					return argument + OFFSET;
				}
			};
		}
	}

	public interface ICommand extends ITestCommandInterface {
		public int method(int argument);
	}

	@CommandFactory(FactoriedCommandFactory.class)
	public interface IFactoriedCommand extends ITestCommandInterface {
		public int method(int argument);
	}

	@Test
	public void returnProcessInvocationException() {
		final ICommandProxyFactory factory = new CommandProxyFactory(DumbCommandConverter.create(), null);
		try {
			factory.apply(ICommand.class).method(0);
		} catch (ReturnProcessInvocationException exception) {
			final ProcessInvocation<?> processInvocation = exception.getProcessInvocation();
			final CommandInvocation<IRedirect, IRedirect> commandInvocation = processInvocation.getCommandInvocation();
			HAssert.assertEquals(HCollection.asList("method", "0"), commandInvocation.getArguments());
			HAssert.assertNull(commandInvocation.getWorking());
			HAssert.assertEquals(commandInvocation.getIo(), StandardIO.of(InheritRedirect.create()));
			HAssert.assertSame(IntegerResultSupplier.create(), processInvocation.getResultSupplier());
		}
	}

	@Test
	public void commandFactory() {
		final ICommandProxyFactory factory = new CommandProxyFactory(DumbCommandConverter.create(), null);
		final IFactoriedCommand command = factory.apply(IFactoriedCommand.class);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				HAssert.assertEquals(i + (FactoriedCommandFactory.OFFSET = j), command.method(i));
			}
		}
	}
}
