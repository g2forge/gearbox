package com.g2forge.gearbox.command.v1.runner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.java.core.helpers.HArray;
import com.g2forge.alexandria.java.core.helpers.HString;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.command.runner.IProcess;
import com.g2forge.gearbox.command.runner.ProcessBuilderRunner;
import com.g2forge.gearbox.command.runner.redirect.IRedirect;
import com.g2forge.gearbox.command.v1.control.Command;

public class TestProxifier extends ATestRunner {
	public interface ForTesting extends ITestCommandInterface {
		@Command("false")
		public IProcess false_();
	}

	@Test(expected = RuntimeException.class)
	public void assertSuccess() {
		proxifier.generate(getRunner(), ForTesting.class).false_().assertSuccess();
	}

	protected IFunction1<CommandInvocation<IRedirect, IRedirect>, IProcess> createRunner() {
		return new ProcessBuilderRunner(null);
	}

	@Test
	public void echo() {
		final String[] args = HArray.create("foo\\n!", "bar");
		final String expected = HString.unescape(Stream.of(args).collect(Collectors.joining(" "))) + "\n";
		final String actual = getUtils().echo(true, args);
		Assert.assertEquals(expected, actual);
	};

	@Test
	public void pwd() {
		final Path path = Paths.get("").toAbsolutePath();
		final String result = getUtils().pwd(path, true).trim();
		final int slash = result.lastIndexOf('/');
		final String actual = (slash >= 0) ? result.substring(slash + 1) : result;
		Assert.assertEquals(path.getFileName().toString(), actual);
	}
}
