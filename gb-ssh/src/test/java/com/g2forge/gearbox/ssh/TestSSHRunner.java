package com.g2forge.gearbox.ssh;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Test;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.alexandria.test.HAssume;
import com.g2forge.alexandria.wizard.PropertyStringInput;
import com.g2forge.alexandria.wizard.UserStringInput;
import com.g2forge.gearbox.command.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.converter.dumb.Command;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.proxy.method.ICommandInterface;
import com.g2forge.gearbox.command.test.ATestCommand;

public class TestSSHRunner extends ATestCommand {
	@After
	public void close() {
		final Object runner;
		try {
			runner = getRunner();
		} catch (Throwable throwable) {
			return;
		}
		((ICloseable) runner).close();
	}

	@Override
	protected ICommandConverterR_ createRenderer() {
		return new DumbCommandConverter();
	}

	@Override
	protected IFunction1<CommandInvocation<IRedirect, IRedirect>, IProcess> createRunner() {
		return new SSHRunner(Duration.ofSeconds(5), TestSSH.getConfig());
	}

	@Test
	public void cwd() {
		HAssume.assumeNotNull(TestSSH.getConfig());
		final String cwd = HAssume.assumeNoException(new PropertyStringInput("sshtest.cwd").fallback(new UserStringInput("SSH Test CWD", false)));
		HAssert.assertEquals(cwd, getUtils().pwd(Paths.get("./"), false).trim());
	}

	@Test
	public void hostname() {
		HAssume.assumeNotNull(TestSSH.getConfig());
		final String hostname = HAssume.assumeNoException(new PropertyStringInput("sshtest.hostname").fallback(new UserStringInput("SSH Test Hostname", false)));
		HAssert.assertEquals(hostname, getUtils().echo(false, "${HOSTNAME}").trim());
	}

	@Test
	public void quoting() {
		HAssume.assumeNotNull(TestSSH.getConfig());
		final String executable = HAssume.assumeNoException(new PropertyStringInput("sshtest.clireport").fallback(new UserStringInput("SSH Test CLIReport executable", false)));
		final ICLIReport clireport = getFactory().apply(ICLIReport.class);

		final List<String> lines = new ArrayList<>(clireport.clireport(executable, "A", "B C").map(String::strip).collect(Collectors.toList()));
		lines.remove(1); // Remove the executable itself from the output, since it might be in a weird path during testing
		HAssert.assertEquals("CLIReport: 3 arguments\n0001: A\n0003: B C", lines.stream().collect(Collectors.joining("\n")));
	}

	public interface ICLIReport extends ICommandInterface {
		@Command({})
		public Stream<String> clireport(String executable, String... args);
	}

	protected boolean isValid() {
		return TestSSH.getConfig() != null;
	}
}
