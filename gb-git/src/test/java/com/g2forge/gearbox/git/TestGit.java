package com.g2forge.gearbox.git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Test;

import com.g2forge.alexandria.java.fluent.optional.NonNullOptional;
import com.g2forge.alexandria.java.io.file.TempDirectory;
import com.g2forge.alexandria.wizard.InputUnspecifiedException;
import com.g2forge.alexandria.wizard.PropertyStringInput;
import com.g2forge.alexandria.wizard.UserPasswordInput;
import com.g2forge.alexandria.wizard.UserStringInput;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

public class TestGit {
	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	public static class TestConfiguration {
		protected final Path keyFile;

		protected final String keyPassphrase;

		protected final String repositoryURI;

		public static TestConfiguration createConfig() {
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			final Future<TestConfiguration> handler = executor.submit(new Callable<TestConfiguration>() {
				@Override
				public TestConfiguration call() throws Exception {
					try {
						final TestConfigurationBuilder builder = TestConfiguration.builder();
						builder.keyFile(Paths.get(new PropertyStringInput("ssh.key.file").fallback(new UserStringInput("SSH Key File", false)).get()));
						builder.keyPassphrase(new PropertyStringInput("ssh.key.passphrase").fallback(new UserPasswordInput("SSH Key Passphrase")).get());
						builder.repositoryURI(new PropertyStringInput("git.uri").fallback(NonNullOptional.of("git@github.com:g2forge/gearbox.git")).get());
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
	}

	@Test
	public void gitClone() throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		final TestConfiguration testConfiguration = TestConfiguration.createConfig();
		try (final TempDirectory temp = new TempDirectory()) {
			final Path home = temp.get().resolve("Home"), ssh = temp.get().resolve(HSSH.SSHDIR), repo = temp.get().resolve("repo");
			Files.createDirectories(home);
			Files.createDirectories(ssh);
			final GitSSHTransportConfigCallback transportConfigCallback = new GitSSHTransportConfigCallback(() -> new GitSSHCredentials(testConfiguration.getKeyFile(), testConfiguration.getKeyPassphrase()), GitSSHTransportConfigCallback.StrictHostKeyChecking.ACCEPT_NEW, home, ssh);
			Git.cloneRepository().setTransportConfigCallback(transportConfigCallback).setDirectory(repo.toFile()).setURI(testConfiguration.getRepositoryURI()).call().close();
		}
	}
}
