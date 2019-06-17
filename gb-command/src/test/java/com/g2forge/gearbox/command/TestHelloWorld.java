package com.g2forge.gearbox.command;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.process.ProcessBuilderRunner;
import com.g2forge.gearbox.command.proxy.CommandProxyFactory;
import com.g2forge.gearbox.command.proxy.ICommandProxyFactory;
import com.g2forge.gearbox.command.proxy.method.ICommandInterface;

public class TestHelloWorld {
	public interface IEcho extends ICommandInterface {
		public String echo(String... args);
	}

	@Test
	public void helloWorld() {
		final ICommandProxyFactory factory = new CommandProxyFactory(DumbCommandConverter.create(), new ProcessBuilderRunner(null));
		final IEcho echo = factory.apply(IEcho.class);
		final String string = "Hello, World!";
		Assert.assertEquals(string, echo.echo(string).trim());
	}
}
