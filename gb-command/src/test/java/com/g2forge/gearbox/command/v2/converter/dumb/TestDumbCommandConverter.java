package com.g2forge.gearbox.command.v2.converter.dumb;

import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.java.core.error.UnreachableCodeError;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.runner.redirect.IRedirect;
import com.g2forge.gearbox.command.v2.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.v2.proxy.ProxyInvocationHandler;
import com.g2forge.gearbox.command.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ReturnProcessInvocationException;
import com.g2forge.gearbox.command.v2.proxy.transformers.MethodToCommandInvocationTransformer;

import lombok.Getter;

public class TestDumbCommandConverter {
	public interface IBooleanFlag {
		public void method(@Flag("-flag") Boolean argument);
	}

	public interface IBooleanNamed {
		public void method(@Named("name=") boolean argument);
	}

	public interface IBooleanValue {
		public void method(Boolean argument);
	}

	public interface IPathNamed {
		public void method(@Named("name=") Path argument);
	}

	public interface IPathValue {
		public void method(Path argument);
	}

	public interface IPathWorking {
		public void method(@Working Path argument);
	}

	public interface IStringArrayNamed {
		public void method(@Named("name=") String... argument);
	}

	public interface IStringArrayValue {
		public void method(String... argument);
	}

	public interface IStringNamed {
		public void method(@Named("name=") String argument);
	}

	public interface IStringValue {
		public void method(String argument);
	}

	@Getter(lazy = true)
	private static final ProxyInvocationHandler handler = createHandler();

	public static <T> void assertCommand(Class<T> type, IConsumer1<? super T> invoke, Path expectedWorking, String... expectedArguments) {
		@SuppressWarnings("unchecked")
		final T proxy = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, getHandler());
		try {
			invoke.accept(proxy);
			throw new UnreachableCodeError();
		} catch (ReturnProcessInvocationException exception) {
			final CommandInvocation<IRedirect, IRedirect> commandInvocation = exception.getProcessInvocation().getCommandInvocation();
			HAssert.assertEquals(expectedWorking, commandInvocation.getWorking());
			HAssert.assertEquals(HCollection.asList(expectedArguments), commandInvocation.getArguments());
		}
	}

	protected static ProxyInvocationHandler createHandler() {
		final ProxyInvocationHandler handler = new ProxyInvocationHandler(new MethodToCommandInvocationTransformer() {
			@Override
			protected ICommandConverterR_ getRenderer(MethodInvocation methodInvocation) {
				return new DumbCommandConverter();
			}
		}, null);
		return handler;
	}

	@Test
	public void booleanFlag() {
		assertCommand(IBooleanFlag.class, x -> x.method(true), null, "method", "-flag");
		assertCommand(IBooleanFlag.class, x -> x.method(false), null, "method");
	}

	@Test
	public void booleanNamed() {
		assertCommand(IBooleanNamed.class, x -> x.method(true), null, "method", "name=true");
	}

	@Test
	public void booleanValue() {
		assertCommand(IBooleanValue.class, x -> x.method(false), null, "method", "false");
	}

	@Test
	public void pathNamed() {
		assertCommand(IPathNamed.class, x -> x.method(Paths.get("A")), null, "method", "name=A");
	}

	@Test
	public void pathValue() {
		assertCommand(IPathValue.class, x -> x.method(Paths.get("A")), null, "method", "A");
	}

	@Test
	public void pathWorking() {
		assertCommand(IPathWorking.class, x -> x.method(Paths.get("A")), Paths.get("A"), "method");
	}

	@Test(expected = IllegalArgumentException.class)
	public void stringArrayNamed() {
		assertCommand(IStringArrayNamed.class, x -> x.method("A", "B"), null);
	}

	@Test
	public void stringArrayValue() {
		assertCommand(IStringArrayValue.class, x -> x.method(), null, "method");
		assertCommand(IStringArrayValue.class, x -> x.method("A"), null, "method", "A");
		assertCommand(IStringArrayValue.class, x -> x.method("A", "B"), null, "method", "A", "B");
	}

	@Test
	public void stringNamed() {
		assertCommand(IStringNamed.class, x -> x.method("A"), null, "method", "name=A");
	}

	@Test
	public void stringValue() {
		assertCommand(IStringValue.class, x -> x.method("A"), null, "method", "A");
	}
}
