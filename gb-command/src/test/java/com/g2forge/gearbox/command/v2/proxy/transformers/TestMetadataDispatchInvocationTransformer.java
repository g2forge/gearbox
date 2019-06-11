package com.g2forge.gearbox.command.v2.proxy.transformers;

import org.junit.Test;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.v1.runner.redirect.IRedirect;
import com.g2forge.gearbox.command.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.v2.proxy.method.OverrideInvocationTransformer;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.v2.proxy.transformers.IInvocationTransformer;
import com.g2forge.gearbox.command.v2.proxy.transformers.MetadataDispatchInvocationTransformer;

import lombok.RequiredArgsConstructor;

public class TestMetadataDispatchInvocationTransformer {
	public interface IDelegate {
		public int method();
	}

	public interface IOverride {
		@OverrideInvocationTransformer(MyOverrideInvocationTransformer.class)
		public int method();
	}

	@RequiredArgsConstructor
	public static class MyOverrideInvocationTransformer implements IInvocationTransformer {
		@Override
		public ProcessInvocation<?> apply(MethodInvocation methodInvocation) {
			return new ProcessInvocation<>(null, process -> 4);
		}
	}

	@Test
	public void delegate() {
		final CommandInvocation<IRedirect, IRedirect> commandInvocation = CommandInvocation.<IRedirect, IRedirect>builder().build();
		final MethodInvocation methodInvocation = new MethodInvocation(new IDelegate() {
			@Override
			public int method() {
				return 0;
			}
		}, IDelegate.class.getDeclaredMethods()[0], HCollection.emptyList());
		final ProcessInvocation<?> processInvocation = new MetadataDispatchInvocationTransformer(new CustomInvocationTransformer(commandInvocation, 2)).apply(methodInvocation);
		HAssert.assertSame(commandInvocation, processInvocation.getInvocation());
		HAssert.assertEquals(2, processInvocation.getResultSupplier().apply(null));
	}

	@Test
	public void override() {
		final MethodInvocation methodInvocation = new MethodInvocation(new IOverride() {
			@Override
			public int method() {
				return 0;
			}
		}, IOverride.class.getDeclaredMethods()[0], HCollection.emptyList());
		final ProcessInvocation<?> processInvocation = new MetadataDispatchInvocationTransformer(new CustomInvocationTransformer(CommandInvocation.<IRedirect, IRedirect>builder().build(), 2)).apply(methodInvocation);
		HAssert.assertNull(processInvocation.getInvocation());
		HAssert.assertEquals(4, processInvocation.getResultSupplier().apply(null));
	}
}
