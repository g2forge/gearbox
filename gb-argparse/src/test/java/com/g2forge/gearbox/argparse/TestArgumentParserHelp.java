package com.g2forge.gearbox.argparse;

import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IThrowRunnable;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.alexandria.test.HMatchers;

public class TestArgumentParserHelp {
	@Test
	public void unparseable() {
		assertHelp("\n\nArguments: <unparseable>\n", TestArgumentParser.Unparseable.class);
	}

	private void assertHelp(final String help, final Class<?> type) {
		final IThrowRunnable<RuntimeException> runnable = () -> ArgumentParser.parse(type, HCollection.asList("--help"));
		HAssert.assertThat(runnable, HMatchers.isThrowable(ArgumentHelpException.class, HMatchers.equalTo(help)));
	}

	@Test
	public void array() {
		assertHelp("\n\nArguments: <strings>\n", TestArgumentParser.Array.class);
	}

	@Test
	public void flag() {
		assertHelp("\n\nNo Positional Arguments\n\t--flag\n", TestArgumentParser.Flag.class);
	}

	@Test
	public void mixed() {
		assertHelp("\n\nArguments: <path> [...]\n\tpath   - A path\n\t--flag - An optional flag\n", TestArgumentParser.Mixed.class);
	}

	@Test
	public void none() {
		assertHelp("\n\nNo Positional Arguments\n", TestArgumentParser.None.class);
	}

	@Test
	public void ordered() {
		assertHelp("\n\nArguments: <string>\n", TestArgumentParser.Ordered.class);
	}
}
