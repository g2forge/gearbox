package com.g2forge.gearbox.functional.runner;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.alexandria.wizard.PropertyStringInput;
import com.g2forge.alexandria.wizard.UserPasswordInput;
import com.g2forge.alexandria.wizard.UserStringInput;
import com.g2forge.gearbox.functional.runner.SSHServer.SSHServerBuilder;

import lombok.Getter;

public class TestSSHRunner extends ATestRunner {
	@Getter(lazy = true)
	private static final SSHServer server = createServer();

	protected static SSHServer createServer() {
		final SSHServerBuilder builder = SSHServer.builder();
		builder.username(new PropertyStringInput("ssh.username").fallback(new UserStringInput("SSH Username", false)).get());
		builder.password(new PropertyStringInput("ssh.password").fallback(new UserPasswordInput("SSH Password")).get());
		builder.host(new PropertyStringInput("ssh.host").fallback(new UserStringInput("SSH Host", false)).get());
		builder.port(Integer.valueOf(new PropertyStringInput("ssh.port").fallback(new UserStringInput("SSH Port", false)).get()));
		final SSHServer server = builder.build();
		return server;
	}

	@After
	public void close() {
		((ICloseable) getRunner()).close();
	}

	@Override
	protected IRunner createRunner() {
		return new SSHRunner(getServer());
	}

	@Test
	public void cwd() {
		Assert.assertEquals(new PropertyStringInput("sshtest.cwd").fallback(new UserStringInput("SSH Test CWD", false)).get(), getUtils().pwd(Paths.get("./"), false).trim());
	}

	@Test
	public void hostname() {
		Assert.assertEquals(new PropertyStringInput("sshtest.hostname").fallback(new UserStringInput("SSH Test Hostname", false)).get(), getUtils().echo(false, "${HOSTNAME}").trim());
	}
}
