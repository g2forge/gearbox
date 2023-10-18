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

import com.g2forge.alexandria.command.invocation.CommandInvocation;
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
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.test.ATestCommand;

import lombok.Getter;

public class TestSSHRunner extends ATestCommand {
	@Getter(lazy = true)
	private static final SSHConfig config = createConfig();

	protected static SSHConfig createConfig() {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<SSHConfig> handler = executor.submit(new Callable<SSHConfig>() {
			@Override
			public SSHConfig call() throws Exception {
				try {
					final SSHRemote.SSHRemoteBuilder remote = SSHRemote.builder();
					final SSHCredentials.SSHCredentialsBuilder credentials = SSHCredentials.builder();
					remote.username(new PropertyStringInput("ssh.username").fallback(new UserStringInput("SSH Username", false)).get());
					credentials.password(new PropertyStringInput("ssh.password").fallback(new UserPasswordInput("SSH Password")).get());
					remote.host(new PropertyStringInput("ssh.host").fallback(new UserStringInput("SSH Host", false)).get());
					remote.port(Integer.valueOf(new PropertyStringInput("ssh.port").fallback(new UserStringInput("SSH Port", false)).get()));
					return new SSHConfig(remote.build(), credentials.build());
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
	protected IFunction1<CommandInvocation<IRedirect, IRedirect>, IProcess> createRunner() {
		return new SSHRunner(getConfig());
	}

	@Test
	public void cwd() {
		HAssume.assumeNotNull(getConfig());
		final String cwd = HAssume.assumeNoException(new PropertyStringInput("sshtest.cwd").fallback(new UserStringInput("SSH Test CWD", false)));
		HAssert.assertEquals(cwd, getUtils().pwd(Paths.get("./"), false).trim());
	}

	@Test
	public void hostname() {
		HAssume.assumeNotNull(getConfig());
		final String hostname = HAssume.assumeNoException(new PropertyStringInput("sshtest.hostname").fallback(new UserStringInput("SSH Test Hostname", false)));
		HAssert.assertEquals(hostname, getUtils().echo(false, "${HOSTNAME}").trim());
	}

	protected boolean isValid() {
		return getConfig() != null;
	}
}
