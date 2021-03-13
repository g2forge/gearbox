package com.g2forge.gearbox.command.proxy.transformers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import org.junit.Test;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.helpers.HStream;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.proxy.method.ITestCommandInterface;
import com.g2forge.gearbox.command.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.proxy.process.ModifyProcessInvocationException;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;

public class TestDefaultMethodInvocationTransformer {
	public interface IDefaultModify extends ITestCommandInterface {
		public default int method() {
			throw new ModifyProcessInvocationException(processInvocation -> new ProcessInvocation<>(processInvocation.getCommandInvocation(), process -> 3, processInvocation.getEnvironmentVariables()));
		}
	}

	public interface IDefaultReturn extends ITestCommandInterface {
		public default int method() {
			return 1;
		}
	}

	public interface INoDefault extends ITestCommandInterface {
		public int method();
	}

	@Test
	public void defaultModify() {
		final CommandInvocation<IRedirect, IRedirect> commandInvocation = CommandInvocation.<IRedirect, IRedirect>builder().build();
		
		final Method[] methods = IDefaultModify.class.getDeclaredMethods();
		final Method method = HStream.findOne(Stream.of(methods).filter(m -> !Modifier.isStatic(m.getModifiers())));
		final MethodInvocation methodInvocation = new MethodInvocation(new IDefaultModify() {}, method, HCollection.emptyList());
		
		final ProcessInvocation<?> processInvocation = new DefaultMethodInvocationTransformer(new CustomInvocationTransformer(commandInvocation, 2)).apply(methodInvocation);
		HAssert.assertSame(commandInvocation, processInvocation.getCommandInvocation());
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
		HAssert.assertSame(commandInvocation, processInvocation.getCommandInvocation());
		HAssert.assertEquals(2, processInvocation.getResultSupplier().apply(null));
	}
}
