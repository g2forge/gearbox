package com.g2forge.gearbox.command.v2.outdated;

import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.v2.converter.IMethodArgument;
import com.g2forge.gearbox.command.v2.outdated.ArgumentConsumer;
import com.g2forge.gearbox.command.v2.outdated.IArgumentConsumer;
import com.g2forge.gearbox.command.v2.outdated.IMethodConsumer;
import com.g2forge.gearbox.command.v2.outdated.MethodConsumer;
import com.g2forge.gearbox.command.v2.outdated.MethodToCommandInvocationTransformer;
import com.g2forge.gearbox.command.v2.process.IProcess;
import com.g2forge.gearbox.command.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.IResultSupplier;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation.ProcessInvocationBuilder;

import lombok.Data;

public class TestMethodToCommandInvocationTransformer {
	public static interface Argument {
		public void method(@ArgumentConsumer(FakeArgumentConsumer.class) String parameter);
	}

	public static class FakeArgumentConsumer implements IArgumentConsumer {
		@Override
		public void accept(ProcessInvocationBuilder<Object> processInvocationBuilder, MethodInvocation methodInvocation, IMethodArgument<?> argument) {
			processInvocationBuilder.resultSupplier(new FakeResultSupplier((String) argument.get()));
		}
	}

	public static class FakeMethodConsumer implements IMethodConsumer {
		@Override
		public void accept(ProcessInvocationBuilder<Object> processInvocationBuilder, MethodInvocation methodInvocation) {
			processInvocationBuilder.resultSupplier(new FakeResultSupplier("B"));
		}
	}

	@Data
	public static class FakeResultSupplier implements IResultSupplier<String> {
		protected final String retVal;

		@Override
		public String apply(IProcess process) {
			return retVal;
		}
	}

	public static interface Method {
		@MethodConsumer(FakeMethodConsumer.class)
		public void method();
	}

	@Test
	public void argument() {
		final MethodInvocation methodInvocation = new MethodInvocation(new Argument() {
			@Override
			public void method(String parameter) {
				HAssert.fail();
			}
		}, Argument.class.getDeclaredMethods()[0], HCollection.asList("A"));
		final ProcessInvocation<?> processInvocation = new MethodToCommandInvocationTransformer().apply(methodInvocation);
		HAssert.assertNull(processInvocation.getCommandInvocation());
		HAssert.assertEquals("A", processInvocation.getResultSupplier().apply(null));
	}

	@Test
	public void method() {
		final MethodInvocation methodInvocation = new MethodInvocation(new Method() {
			@Override
			public void method() {
				HAssert.fail();
			}
		}, Method.class.getDeclaredMethods()[0], HCollection.emptyList());
		final ProcessInvocation<?> processInvocation = new MethodToCommandInvocationTransformer().apply(methodInvocation);
		HAssert.assertNull(processInvocation.getCommandInvocation());
		HAssert.assertEquals("B", processInvocation.getResultSupplier().apply(null));
	}
}
