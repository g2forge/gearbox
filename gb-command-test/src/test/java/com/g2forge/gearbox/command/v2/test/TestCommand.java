package com.g2forge.gearbox.command.v2.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HArray;
import com.g2forge.alexandria.java.core.helpers.HString;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.v2.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.v2.converter.dumb.Command;
import com.g2forge.gearbox.command.v2.converter.dumb.DumbCommandConverter;

public class TestCommand extends ATestCommand {
	public interface ForTesting extends ITestCommandInterface {
		@Command("false")
		public IProcess false_();
	}

	@Test(expected = RuntimeException.class)
	public void assertSuccess() {
		getFactory().apply(ForTesting.class).false_().assertSuccess();
	}

	@Override
	protected ICommandConverterR_ createRenderer() {
		return DumbCommandConverter.create();
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
