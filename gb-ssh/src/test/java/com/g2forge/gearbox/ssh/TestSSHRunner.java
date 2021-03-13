package com.g2forge.gearbox.ssh;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Test;

import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.alexandria.test.HAssume;
import com.g2forge.alexandria.wizard.InputUnspecifiedException;
import com.g2forge.alexandria.wizard.PropertyStringInput;
import com.g2forge.alexandria.wizard.UserPasswordInput;
import com.g2forge.alexandria.wizard.UserStringInput;
import com.g2forge.gearbox.command.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.test.ATestCommand;
import com.g2forge.gearbox.ssh.SSHServer.SSHServerBuilder;

import lombok.Getter;

public class TestSSHRunner extends ATestCommand {
	@Getter(lazy = true)
	private static final SSHServer server = createServer();

	protected static SSHServer createServer() {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<SSHServer> handler = executor.submit(new Callable<SSHServer>() {
			@Override
			public SSHServer call() throws Exception {
				try {
					final SSHServerBuilder builder = SSHServer.builder();
					builder.username(new PropertyStringInput("ssh.username").fallback(new UserStringInput("SSH Username", false)).get());
					builder.password(new PropertyStringInput("ssh.password").fallback(new UserPasswordInput("SSH Password")).get());
					builder.host(new PropertyStringInput("ssh.host").fallback(new UserStringInput("SSH Host", false)).get());
					builder.port(Integer.valueOf(new PropertyStringInput("ssh.port").fallback(new UserStringInput("SSH Port", false)).get()));
					return builder.build();
				} catch (InputUnspecifiedException exception) {
					return null;
				}
			}
		});
		try {
			return handler.get(Duration.ofSeconds(60).toMillis(), TimeUnit.MILLISECONDS);
		} catch (TimeoutException | InterruptedException | ExecutionException exception) {
			handler.cancel(true);
			return null;
		} finally {
			executor.shutdownNow();
		}
	}

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
	protected IFunction1<ProcessInvocation<?>, IProcess> createRunner() {
		return new SSHRunner(getServer());
	}

	@Test
	public void cwd() {
		HAssume.assumeNotNull(getServer());
		final String cwd = HAssume.assumeNoException(new PropertyStringInput("sshtest.cwd").fallback(new UserStringInput("SSH Test CWD", false)));
		HAssert.assertEquals(cwd, getUtils().pwd(Paths.get("./"), false).trim());
	}

	@Test
	public void hostname() {
		HAssume.assumeNotNull(getServer());
		final String hostname = HAssume.assumeNoException(new PropertyStringInput("sshtest.hostname").fallback(new UserStringInput("SSH Test Hostname", false)));
		HAssert.assertEquals(hostname, getUtils().echo(false, "${HOSTNAME}").trim());
	}

	protected boolean isValid() {
		return getServer() != null;
	}
}
