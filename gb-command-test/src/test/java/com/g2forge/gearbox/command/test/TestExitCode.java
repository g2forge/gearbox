package com.g2forge.gearbox.command.test;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.g2forge.alexandria.command.clireport.HCLIReport;
import com.g2forge.alexandria.java.core.helpers.HCollector;
import com.g2forge.alexandria.java.platform.HPlatform;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.converter.dumb.Command;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.converter.dumb.EnvPath;

public class TestExitCode extends ATestCommand {
	public interface IGenericCommand extends ITestCommandInterface {
		@Command({})
		public Stream<String> run(@EnvPath(usage = EnvPath.Usage.AddFirst) Path path, String filename, String... args);
	}

	@Override
	protected ICommandConverterR_ createRenderer() {
		return DumbCommandConverter.create();
	}

	protected boolean isValid() {
		// Don't run the generic exit code test
		return false;
	}

	@Test
	public void one() {
		final Path clireport = HCLIReport.download(null).get().toAbsolutePath();
		final String[] arguments = new String[] { "--exit", "1", "A", "B" };
		try {
			getFactory().apply(IGenericCommand.class).run(null, clireport.toString(), arguments).collect(Collectors.toList());
		} catch (RuntimeException e) {
			final HCLIReport.Output expected = HCLIReport.computeExpectedOutput(clireport.toString(), arguments);
			final String message = String.format("Showing last %1$d lines of output:\n", expected.getOutput().size()) + expected.getOutput().stream().map(s -> "\t" + s).collect(HCollector.joining("\n")) + "\n";
			HAssert.assertEquals(message, e.getMessage());
			return;
		}
		HAssert.fail();
	}

	@Test
	public void sleep() {
		final String executable;
		final String[] arguments;

		switch (HPlatform.getPlatform().getCategory()) {
			case Microsoft: {
				executable = "cmd";
				arguments = new String[] { "/c", "sleep 1 && exit 1" };
				break;
			}
			default: {
				executable = "/bin/sh";
				arguments = new String[] { "-c", "sleep 1; exit 1" };
				break;
			}
		}

		for (int i = 0; i < 100; i++) {
			try {
				getFactory().apply(IGenericCommand.class).run(null, executable, arguments).collect(Collectors.toList());
			} catch (RuntimeException e) {
				continue;
			}
			HAssert.fail("Failed to correctly catch the non-zero exit code in iteration " + i + " (there's probably a race condition due to variations in OS behavior around process termination and stream redirection)");
		}
	}

	@Test
	public void zero() {
		final Path clireport = HCLIReport.download(null).get().toAbsolutePath();
		final String[] arguments = new String[] { "--exit", "0", "A", "B" };
		final List<String> actual = getFactory().apply(IGenericCommand.class).run(null, clireport.toString(), arguments).collect(Collectors.toList());
		HAssert.assertEquals(HCLIReport.computeExpectedOutput(clireport.toString(), arguments).getOutput(), actual);
	}
}
