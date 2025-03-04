package com.g2forge.gearbox.command.test;

import java.nio.file.Path;

import org.junit.Test;

import com.g2forge.alexandria.command.clireport.HCLIReport;
import com.g2forge.gearbox.command.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.converter.dumb.Command;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.converter.dumb.EnvPath;

public class TestPath extends ATestCommand {
	public interface IGenericCommand extends ITestCommandInterface {
		@Command({})
		public void run(@EnvPath(usage = EnvPath.Usage.AddFirst) Path path, String filename, String... args);
	}

	@Override
	protected ICommandConverterR_ createRenderer() {
		return DumbCommandConverter.create();
	}

	@Test
	public void explicit() {
		final Path clireport = HCLIReport.download(null).get().toAbsolutePath();
		getFactory().apply(IGenericCommand.class).run(null, clireport.toString(), "argument");
	}

	protected boolean isValid() {
		// Don't run the generic exit code test
		return false;
	}

	@Test
	public void path() {
		final Path clireport = HCLIReport.download(null).get().toAbsolutePath();
		getFactory().apply(IGenericCommand.class).run(clireport.getParent(), clireport.getFileName().toString(), "argument");
	}
}
