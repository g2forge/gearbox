package com.g2forge.gearbox.command.converter.dumb;

import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.Test;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.java.core.error.UnreachableCodeError;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.java.platform.HPlatform;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.proxy.ProxyInvocationHandler;
import com.g2forge.gearbox.command.proxy.method.ITestCommandInterface;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.proxy.process.ReturnProcessInvocationException;
import com.g2forge.gearbox.command.proxy.result.BooleanResultSupplier;
import com.g2forge.gearbox.command.proxy.result.IResultSupplier;
import com.g2forge.gearbox.command.proxy.result.IntegerResultSupplier;
import com.g2forge.gearbox.command.proxy.result.StreamResultSupplier;
import com.g2forge.gearbox.command.proxy.result.StringResultSupplier;
import com.g2forge.gearbox.command.proxy.result.VoidResultSupplier;
import com.g2forge.gearbox.command.proxy.transformers.MethodToCommandInvocationTransformer;

import lombok.Getter;

public class TestDumbCommandConverter {
	public interface IBooleanFlag extends ITestCommandInterface {
		public Boolean method(@Flag("-flag") Boolean argument);
	}

	public interface IBooleanNamed extends ITestCommandInterface {
		public boolean method(@Named("name=") boolean argument);
	}

	public interface IBooleanValue extends ITestCommandInterface {
		public Boolean method(Boolean argument);
	}

	public interface IEnvironment extends ITestCommandInterface {
		public void method(@Environment("a") String a, @Environment("b") Path b);
	}

	public interface IEnvironmentNull extends ITestCommandInterface {
		public void method(@Environment("a") String a);
	}

	public interface IEnvPath extends ITestCommandInterface {
		public void method(@EnvPath(usage = EnvPath.Usage.Replace) Path path);
	}

	public interface IIntegerNamed extends ITestCommandInterface {
		public int method(@Named("name=") Integer argument);
	}

	public interface IIntegerValue extends ITestCommandInterface {
		public Integer method(int argument);
	}

	public interface IPathNamed extends ITestCommandInterface {
		public void method(@Named("name=") Path argument);
	}

	public interface IPathValue extends ITestCommandInterface {
		public void method(Path argument);
	}

	public interface IPathWorking extends ITestCommandInterface {
		public void method(@Working Path argument);
	}

	public interface IStringArrayNamed extends ITestCommandInterface {
		public Stream<String> method(@Named("name=") String... argument);
	}

	public interface IStringArrayValue extends ITestCommandInterface {
		public Stream<String> method(String... argument);
	}

	public interface IStringNamed extends ITestCommandInterface {
		public String method(@Named("name=") String argument);
	}

	public interface IStringNamedNonJoined extends ITestCommandInterface {
		public String method(@Named(value = "name", joined = false) String argument);
	}

	public interface IStringValue extends ITestCommandInterface {
		public String method(String argument);
	}

	@Getter(lazy = true)
	private static final ProxyInvocationHandler handler = computeHandler();

	public static <T> CommandInvocation<IRedirect, IRedirect> assertCommand(Class<T> type, IConsumer1<? super T> invoke, Class<? extends IResultSupplier<?>> expectedResultSupplierType, Path expectedWorking, String... expectedArguments) {
		final T proxy = createProxy(type);
		try {
			invoke.accept(proxy);
			throw new UnreachableCodeError();
		} catch (ReturnProcessInvocationException exception) {
			final ProcessInvocation<?> processInvocation = exception.getProcessInvocation();

			final CommandInvocation<IRedirect, IRedirect> commandInvocation = processInvocation.getCommandInvocation();
			HAssert.assertEquals(expectedWorking, commandInvocation.getWorking());
			HAssert.assertEquals(HCollection.asList(expectedArguments), commandInvocation.getArguments());
			HAssert.assertEquals(expectedResultSupplierType, processInvocation.getResultSupplier().getClass());
			return commandInvocation;
		}
	}

	protected static ProxyInvocationHandler computeHandler() {
		return new ProxyInvocationHandler(new MethodToCommandInvocationTransformer(DumbCommandConverter.create()), null);
	}

	protected static <T> T createProxy(Class<T> type) {
		@SuppressWarnings("unchecked")
		final T proxy = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, getHandler());
		return proxy;
	}

	@Test
	public void booleanFlag() {
		assertCommand(IBooleanFlag.class, x -> x.method(true), BooleanResultSupplier.class, null, "method", "-flag");
		assertCommand(IBooleanFlag.class, x -> x.method(false), BooleanResultSupplier.class, null, "method");
	}

	@Test
	public void booleanNamed() {
		assertCommand(IBooleanNamed.class, x -> x.method(true), BooleanResultSupplier.class, null, "method", "name=true");
	}

	@Test
	public void booleanValue() {
		assertCommand(IBooleanValue.class, x -> x.method(false), BooleanResultSupplier.class, null, "method", "false");
	}

	@Test
	public void environment() {
		final String a = "aval", b = "bval";
		final CommandInvocation<IRedirect, IRedirect> command = assertCommand(IEnvironment.class, x -> x.method(a, Paths.get(b)), VoidResultSupplier.class, null, "method");
		HAssert.assertEquals(a, command.getEnvironment().apply("a"));
		HAssert.assertEquals(b, command.getEnvironment().apply("b"));
	}

	@Test
	public void environmentNull() {
		final CommandInvocation<IRedirect, IRedirect> command = assertCommand(IEnvironmentNull.class, x -> x.method(null), VoidResultSupplier.class, null, "method");
		HAssert.assertNull(command.getEnvironment().apply("a"));
	}

	@Test
	public void envPath() {
		final String value = "path";
		final CommandInvocation<IRedirect, IRedirect> command = assertCommand(IEnvPath.class, x -> x.method(Paths.get(value)), VoidResultSupplier.class, null, "method");
		HAssert.assertEquals(value, command.getEnvironment().apply(HPlatform.PATH));
	}

	@Test
	public void intNamed() {
		assertCommand(IIntegerNamed.class, x -> x.method(0), IntegerResultSupplier.class, null, "method", "name=0");
	}

	@Test
	public void intValue() {
		assertCommand(IIntegerValue.class, x -> x.method(1), IntegerResultSupplier.class, null, "method", "1");
	}

	@Test
	public void pathNamed() {
		assertCommand(IPathNamed.class, x -> x.method(Paths.get("A")), VoidResultSupplier.class, null, "method", "name=A");
	}

	@Test
	public void pathValue() {
		assertCommand(IPathValue.class, x -> x.method(Paths.get("A")), VoidResultSupplier.class, null, "method", "A");
	}

	@Test
	public void pathWorking() {
		assertCommand(IPathWorking.class, x -> x.method(Paths.get("A")), VoidResultSupplier.class, Paths.get("A"), "method");
	}

	@Test(expected = IllegalArgumentException.class)
	public void stringArrayNamed() {
		assertCommand(IStringArrayNamed.class, x -> x.method("A", "B"), StringResultSupplier.class, null);
	}

	@Test
	public void stringArrayValue() {
		assertCommand(IStringArrayValue.class, x -> x.method(), StreamResultSupplier.class, null, "method");
		assertCommand(IStringArrayValue.class, x -> x.method("A"), StreamResultSupplier.class, null, "method", "A");
		assertCommand(IStringArrayValue.class, x -> x.method("A", "B"), StreamResultSupplier.class, null, "method", "A", "B");
	}

	@Test
	public void stringNamed() {
		assertCommand(IStringNamed.class, x -> x.method("A"), StringResultSupplier.class, null, "method", "name=A");
	}

	@Test
	public void stringNamedNonJoined() {
		assertCommand(IStringNamedNonJoined.class, x -> x.method("A"), StringResultSupplier.class, null, "method", "name", "A");
	}

	@Test
	public void stringNamedNull() {
		final IStringNamed proxy = createProxy(IStringNamed.class);
		HAssert.assertException(NullPointerException.class, () -> proxy.method(null));
	}

	@Test
	public void stringValue() {
		assertCommand(IStringValue.class, x -> x.method("A"), StringResultSupplier.class, null, "method", "A");
	}

	@Test
	public void stringValueNull() {
		final IStringValue proxy = createProxy(IStringValue.class);
		HAssert.assertException(NullPointerException.class, () -> proxy.method(null));
	}
}
