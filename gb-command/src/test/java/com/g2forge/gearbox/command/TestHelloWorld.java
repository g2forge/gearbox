package com.g2forge.gearbox.command;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.process.ProcessBuilderRunner;
import com.g2forge.gearbox.command.proxy.CommandProxyFactory;
import com.g2forge.gearbox.command.proxy.ICommandProxyFactory;
import com.g2forge.gearbox.command.proxy.method.ICommandInterface;

public class TestHelloWorld {
	/**
	 * A command interface for "echo". Note that the {@link ICommandInterface} parent is empty, and totally optional.
	 */
	public interface IEcho extends ICommandInterface {
		/**
		 * Run the "echo" command over the specified string arguments. Note that no annotations are necessary to control command line generation, because "echo"
		 * is such a simple command. For more complex commands you may need the annotations that go with {@link DumbCommandConverter}.
		 * 
		 * @param args The arguments to echo
		 * @return The output of echo from standard out.
		 */
		public String echo(String... args);
	}

	@Test
	public void helloWorld() {
		// Create a new command proxy factory
		// Use the "dumb" converter to render command line arguments from method arguments
		// Use process builder to run the resulting commands
		final ICommandProxyFactory factory = new CommandProxyFactory(DumbCommandConverter.create(), new ProcessBuilderRunner(null));

		// Get a magical implementation of the interface
		final IEcho echo = factory.apply(IEcho.class);

		// Make sure it works correctly
		final String string = "Hello, World!";
		Assert.assertEquals(string, echo.echo(string).trim(/* We want to trim the new line that echo adds */));
	}
}
