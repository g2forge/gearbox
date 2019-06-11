package com.g2forge.gearbox.functional.v2.proxy;

import java.lang.reflect.Proxy;

import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.functional.v2.process.ProcessInvocation;
import com.g2forge.gearbox.functional.v2.process.ReturnProcessInvocationException;

public class TestProxyInvocationHandler {
	public interface ICommand {
		public int method(int argument);
	}

	@Test
	public void testReturnProcessInvocationException() {
		final ProcessInvocation<?>[] processInvocation = new ProcessInvocation<?>[1];
		final ICommand[] object = new ICommand[1];
		object[0] = (ICommand) Proxy.newProxyInstance(ICommand.class.getClassLoader(), new Class[] { ICommand.class }, new ProxyInvocationHandler(methodInvocation -> {
			HAssert.assertEquals("method", methodInvocation.getMethod().getName());
			HAssert.assertSame(object[0], methodInvocation.getObject());
			HAssert.assertEquals(HCollection.asList(1), methodInvocation.getArguments());

			final ProcessInvocation<Object> retVal = new ProcessInvocation<>(null, null);
			processInvocation[0] = retVal;
			return retVal;
		}, null));

		try {
			object[0].method(1);
		} catch (ReturnProcessInvocationException exception) {
			HAssert.assertSame(processInvocation[0], exception.getProcessInvocation());
		}
	}
}
