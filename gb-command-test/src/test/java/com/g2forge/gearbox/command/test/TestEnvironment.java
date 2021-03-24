package com.g2forge.gearbox.command.test;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.command.invocation.runner.ICommandRunner;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.platform.HPlatform;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.converter.dumb.EnvPath;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.ProcessBuilderRunner;
import com.g2forge.gearbox.command.process.redirect.IRedirect;

public class TestEnvironment extends ATestCommand {
	public interface ICustomCommand {
		public String echo(@EnvPath(usage = EnvPath.Usage.Replace) Path path, String... arguments);
	}

	@Override
	protected ICommandConverterR_ createRenderer() {
		return DumbCommandConverter.create();
	}

	@Override
	protected IFunction1<CommandInvocation<IRedirect, IRedirect>, IProcess> createRunner() {
		return new ProcessBuilderRunner(ICommandRunner.create(HPlatform.getPlatform().getShell()));
	}

	@Test
	public void custom() {
		final String expected = "Hello World!";
		final ICustomCommand custom = getFactory().apply(ICustomCommand.class);
		// The process builder & shell will append paths
		final String actual = custom.echo(Paths.get(expected), HPlatform.getPlatform().getShell().getVariable().createString(false, HPlatform.PATH)).trim();
		HAssert.assertThat(actual, Matchers.startsWith(expected));
	}

	@Test
	public void path() {
		final String expected = System.getenv(HPlatform.PATH);
		// The process builder & shell will append paths
		final String actual = getUtils().echo(false, HPlatform.getPlatform().getShell().getVariable().createString(false, HPlatform.PATH)).trim();
		HAssert.assertThat(actual, Matchers.startsWith(expected));
	}
}
