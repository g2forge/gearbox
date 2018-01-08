package com.g2forge.gearbox.functional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HArray;
import com.g2forge.alexandria.java.core.helpers.HString;
import com.g2forge.gearbox.functional.proxy.IProxifier;
import com.g2forge.gearbox.functional.proxy.Proxifier;
import com.g2forge.gearbox.functional.runner.IRunner;
import com.g2forge.gearbox.functional.runner.ProcessBuilderRunner;

public class TestProxyGenerator {
	protected final IRunner runner = new ProcessBuilderRunner();

	protected final IProxifier proxifier = new Proxifier();

	@Test
	public void test() {
		final String[] args = HArray.create("foo\\n!", "bar");
		final String expected = HString.unescape(Stream.of(args).collect(Collectors.joining(" "))) + "\n";
		final String actual = proxifier.generate(runner, IUtils.class).echo(true, args);
		Assert.assertEquals(expected, actual);
	}
}
