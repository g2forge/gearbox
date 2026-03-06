package com.g2forge.gearbox.command.log;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.marker.ICommand;
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

public class TestLoggingRunner {
	public static class TestArgumentRenderer extends ASimpleArgumentRenderer<List<String>> {
		@Override
		protected List<String> renderSimple(IMethodArgument<List<String>> argument) {
			return argument.get();
		}
	}

	public interface ILogTestCommand extends ICommand {
		public IProcess test(@Constant("constant") @Working Path working, @Named(value = "--password", joined = false) @Password String password, @Environment("ENV_KEY") @Log(Log.Mode.NOTHING) String env, @ArgumentRenderer(TestArgumentRenderer.class) List<String> custom);
	}

	@Test
	public void password() {
		final List<String> log = new ArrayList<>();
		final CommandProxyFactory commandProxyFactory = new CommandProxyFactory(DumbCommandConverter.create(), new LoggingRunner(log::add, i -> null));
		HAssert.assertNull(commandProxyFactory.apply(ILogTestCommand.class).test(Paths.get("foo"), "mypassword", "env_value", HCollection.asList("a", "b")));
		Assert.assertEquals(HCollection.asList("Running: test constant --password *** a b", "\tin foo", "\tenvironment: system environment"), log);
	}
}
