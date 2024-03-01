package com.g2forge.gearbox.ssh;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.g2forge.alexandria.wizard.InputUnspecifiedException;
import com.g2forge.alexandria.wizard.PropertyStringInput;
import com.g2forge.alexandria.wizard.UserPasswordInput;
import com.g2forge.alexandria.wizard.UserStringInput;

import lombok.Getter;

public class TestSSH {
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
}
