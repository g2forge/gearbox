package com.g2forge.gearbox.functional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HArray;
import com.g2forge.alexandria.java.core.helpers.HString;
import com.g2forge.gearbox.functional.control.Command;
import com.g2forge.gearbox.functional.proxy.Proxifier;
import com.g2forge.gearbox.functional.runner.IProcess;
import com.g2forge.gearbox.functional.runner.IRunner;
import com.g2forge.gearbox.functional.runner.ProcessBuilderRunner;

public class TestProxifier {
	public interface ForTesting {
		@Command("false")
		public IProcess false_();
	}

	protected final Proxifier proxifier = new Proxifier();

	protected final IRunner runner = new ProcessBuilderRunner(null);

	protected final IUtils utils = proxifier.generate(runner, IUtils.class);

	@Test(expected = RuntimeException.class)
	public void assertSuccess() {
		proxifier.generate(runner, ForTesting.class).false_().assertSuccess();
	}

	@Test
	public void echo() {
		final String[] args = HArray.create("foo\\n!", "bar");
		final String expected = HString.unescape(Stream.of(args).collect(Collectors.joining(" "))) + "\n";
		final String actual = utils.echo(true, args);
		Assert.assertEquals(expected, actual);
	};

	@Test
	public void exitcode() {
		Assert.assertFalse(utils.false_());
		Assert.assertTrue(utils.true_());
	}

	@Test
	public void pwd() {
		final Path path = Paths.get("").toAbsolutePath();
		final String result = utils.pwd(path, true).trim();
		final int slash = result.lastIndexOf('/');
		final String actual = (slash >= 0) ? result.substring(slash + 1) : result;
		Assert.assertEquals(path.getFileName().toString(), actual);
	}
}
