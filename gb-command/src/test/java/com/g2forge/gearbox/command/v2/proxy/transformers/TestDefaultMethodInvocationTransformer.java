package com.g2forge.gearbox.command.v2.proxy.transformers;

import org.junit.Test;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.runner.redirect.IRedirect;
import com.g2forge.gearbox.command.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ModifyProcessInvocationException;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.v2.proxy.transformers.DefaultMethodInvocationTransformer;

public class TestDefaultMethodInvocationTransformer {
	public interface IDefaultModify {
		public default int method() {
			throw new ModifyProcessInvocationException(processInvocation -> new ProcessInvocation<>(processInvocation.getInvocation(), process -> 3));
		}
	}

	public interface IDefaultReturn {
		public default int method() {
			return 1;
		}
	}

	public interface INoDefault {
		public int method();
	}

	@Test
	public void defaultModify() {
		final CommandInvocation<IRedirect, IRedirect> commandInvocation = CommandInvocation.<IRedirect, IRedirect>builder().build();
		final MethodInvocation methodInvocation = new MethodInvocation(new IDefaultModify() {}, IDefaultModify.class.getDeclaredMethods()[0], HCollection.emptyList());
		final ProcessInvocation<?> processInvocation = new DefaultMethodInvocationTransformer(new CustomInvocationTransformer(commandInvocation, 2)).apply(methodInvocation);
		HAssert.assertSame(commandInvocation, processInvocation.getInvocation());
		HAssert.assertEquals(3, processInvocation.getResultSupplier().apply(null));
	}

	@Test
	public void defaultReturn() {
		final MethodInvocation methodInvocation = new MethodInvocation(new IDefaultReturn() {}, IDefaultReturn.class.getDeclaredMethods()[0], HCollection.emptyList());
		final ProcessInvocation<?> processInvocation = new DefaultMethodInvocationTransformer(null).apply(methodInvocation);
		HAssert.assertEquals(1, processInvocation.getResultSupplier().apply(null));
	}

	@Test
	public void noDefault() {
		final CommandInvocation<IRedirect, IRedirect> commandInvocation = CommandInvocation.<IRedirect, IRedirect>builder().build();
		final MethodInvocation methodInvocation = new MethodInvocation(new INoDefault() {
			@Override
			public int method() {
				return 0;
			}
		}, INoDefault.class.getDeclaredMethods()[0], HCollection.emptyList());
		final ProcessInvocation<?> processInvocation = new DefaultMethodInvocationTransformer(new CustomInvocationTransformer(commandInvocation, 2)).apply(methodInvocation);
		HAssert.assertSame(commandInvocation, processInvocation.getInvocation());
		HAssert.assertEquals(2, processInvocation.getResultSupplier().apply(null));
	}
}
