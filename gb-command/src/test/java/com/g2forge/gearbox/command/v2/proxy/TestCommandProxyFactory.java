package com.g2forge.gearbox.command.v2.proxy;

import org.junit.Test;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.v2.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.v2.proxy.method.ITestCommandInterface;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ReturnProcessInvocationException;
import com.g2forge.gearbox.command.v2.proxy.result.IntegerResultSupplier;

public class TestCommandProxyFactory {
	public interface ICommand extends ITestCommandInterface {
		public int method(int argument);
	}

	@Test
	public void testReturnProcessInvocationException() {
		final ICommandProxyFactory factory = new CommandProxyFactory(DumbCommandConverter.create(), null);
		try {
			factory.apply(ICommand.class).method(0);
		} catch (ReturnProcessInvocationException exception) {
			final ProcessInvocation<?> processInvocation = exception.getProcessInvocation();
			final CommandInvocation<IRedirect, IRedirect> commandInvocation = processInvocation.getCommandInvocation();
			HAssert.assertEquals(HCollection.asList("method", "0"), commandInvocation.getArguments());
			HAssert.assertNull(commandInvocation.getWorking());
			HAssert.assertNull(commandInvocation.getIo());
			HAssert.assertSame(IntegerResultSupplier.create(), processInvocation.getResultSupplier());
		}
	}
}
