package com.g2forge.gearbox.command.proxy;

import java.lang.reflect.Proxy;

import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.proxy.method.ITestCommandInterface;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.proxy.process.ReturnProcessInvocationException;

public class TestProxyInvocationHandler {
	public interface ICommand extends ITestCommandInterface {
		public int method(int argument);
	}

	@Test
	public void returnProcessInvocationException() {
		final ProcessInvocation<?>[] processInvocation = new ProcessInvocation<?>[1];
		final ICommand[] object = new ICommand[1];
		object[0] = (ICommand) Proxy.newProxyInstance(ICommand.class.getClassLoader(), new Class[] { ICommand.class }, new ProxyInvocationHandler(methodInvocation -> {
			HAssert.assertEquals("method", methodInvocation.getMethod().getName());
			HAssert.assertSame(object[0], methodInvocation.getObject());
			HAssert.assertEquals(HCollection.asList(1), methodInvocation.getArguments());

			final ProcessInvocation<Object> retVal = new ProcessInvocation<>(null, null,null);
			processInvocation[0] = retVal;
			return retVal;
		}, null));

		try {
			object[0].method(1);
		} catch (ReturnProcessInvocationException exception) {
			HAssert.assertSame(processInvocation[0], exception.getProcessInvocation());
		}
	}

	@Test
	public void testIdentity() {
		final ICommandProxyFactory factory = new CommandProxyFactory(DumbCommandConverter.create(), null);
		final ICommand instance0 = factory.apply(ICommand.class);
		final ICommand instance1 = factory.apply(ICommand.class);
		HAssert.assertEquals("Command proxy for " + ICommand.class.getName(), instance0.toString());
		HAssert.assertEquals("Command proxy for " + ICommand.class.getName(), instance1.toString());
		HAssert.assertNotEquals(instance0, instance1);
		HAssert.assertNotEquals(instance0.hashCode(), instance1.hashCode());
	}
}
