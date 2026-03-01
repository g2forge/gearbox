package com.g2forge.gearbox.command.log;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.marker.ICommand;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.converter.dumb.Environment;
import com.g2forge.gearbox.command.converter.dumb.Named;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.proxy.CommandProxyFactory;

public class TestLoggingRunner {
	public interface ILogTestCommand extends ICommand {
		public IProcess test(@Named(value = "--password", joined = false) @Password String password, @Environment("ENV_KEY") @Log(Log.Mode.NOTHING) String env);
	}

	@Test
	public void password() {
		final List<String> log = new ArrayList<>();
		final CommandProxyFactory commandProxyFactory = new CommandProxyFactory(DumbCommandConverter.create(), new LoggingRunner(log::add, (i, m) -> null));
		HAssert.assertNull(commandProxyFactory.apply(ILogTestCommand.class).test("mypassword", "env_value"));
		Assert.assertEquals(HCollection.asList("Running: test --password ***", "	environment: system environment"), log);
	}
}
