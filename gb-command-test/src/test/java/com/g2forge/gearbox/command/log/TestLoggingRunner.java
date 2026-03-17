package com.g2forge.gearbox.command.log;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.marker.ICommand;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.gearbox.command.converter.argumentrenderer.ASimpleArgumentRenderer;
import com.g2forge.gearbox.command.converter.argumentrenderer.ArgumentRenderer;
import com.g2forge.gearbox.command.converter.dumb.Constant;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.converter.dumb.Environment;
import com.g2forge.gearbox.command.converter.dumb.Named;
import com.g2forge.gearbox.command.converter.dumb.Working;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.proxy.CommandProxyFactory;
import com.g2forge.habitat.inject.InjectedValueDescriptor;
import com.g2forge.habitat.inject.MapInjectedValue;

public class TestLoggingRunner {
	public static class TestArgumentRenderer extends ASimpleArgumentRenderer<List<String>> {
		@Override
		protected List<String> renderSimple(IMethodArgument<List<String>> argument) {
			return argument.get();
		}
	}

	public static class TestLogArgumentRewriter implements ILogArgumentRewriter {
		public static final MapInjectedValue<String> SUFFIX = new MapInjectedValue<>(new InjectedValueDescriptor<>(TestLogArgumentRewriter.class, "suffix", ITypeRef.of(String.class), ""));

		@Override
		public String rewrite(String argument, Map<String, Object> context) {
			final String suffix = SUFFIX.get(context);
			return argument + suffix;
		}
	}

	public interface ILogTestCommand extends ICommand {
		public IProcess test(@Constant("constant") @Working Path working, @Named(value = "--password", joined = false) @PasswordLog String password, @Environment("ENV_KEY") @SkipLog String env, @ArgumentRenderer(TestArgumentRenderer.class) List<String> custom, @LogArgumentRewriter(TestLogArgumentRewriter.class) String rewritten);
	}

	@Test
	public void password() {
		final List<String> log = new ArrayList<>();
		final HashMap<String, Object> context = new HashMap<String, Object>();
		TestLogArgumentRewriter.SUFFIX.inject(context, "x");
		final CommandProxyFactory commandProxyFactory = new CommandProxyFactory(DumbCommandConverter.create(), new LoggingRunner(log::add, i -> null, context));
		HAssert.assertNull(commandProxyFactory.apply(ILogTestCommand.class).test(Paths.get("foo"), "mypassword", "env_value", HCollection.asList("a", "b"), "c"));
		Assert.assertEquals(HCollection.asList("Running: test constant --password *** a b cx", "\tin foo", "\tenvironment: system environment"), log);
	}
}
