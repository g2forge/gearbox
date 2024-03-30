package com.g2forge.gearbox.argparse;

import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IThrowRunnable;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.alexandria.test.HMatchers;

public class TestArgumentParserHelp {
	@Test
	public void unparseable() {
		assertHelp("<unparseable>", TestArgumentParser.Unparseable.class);
	}

	private void assertHelp(final String help, final Class<?> type) {
		final IThrowRunnable<RuntimeException> runnable = () -> ArgumentParser.parse(type, HCollection.asList("--help"));
		HAssert.assertThat(runnable, HMatchers.isThrowable(ArgumentHelpException.class, HMatchers.equalTo(help)));
	}

	@Test
	public void array() {
		assertHelp("<strings>", TestArgumentParser.Array.class);
	}

	@Test
	public void flag() {
		assertHelp("--flag", TestArgumentParser.Flag.class);
	}

	@Test
	public void mixed() {
		assertHelp("<path> [...]\n\npath A path\n--flag An optional flag", TestArgumentParser.Mixed.class);
	}

	@Test
	public void none() {
		assertHelp("", TestArgumentParser.None.class);
	}

	@Test
	public void ordered() {
		assertHelp("<string>", TestArgumentParser.Ordered.class);
	}
}
