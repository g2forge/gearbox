package com.g2forge.gearbox.ssh;

import java.io.IOException;
import java.time.Duration;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.session.forward.ExplicitPortForwardingTracker;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.junit.Test;

import com.g2forge.alexandria.test.HAssert;
import com.g2forge.alexandria.test.HAssume;
import com.g2forge.gearbox.command.IUtils;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.proxy.CommandProxyFactory;

public class TestSSHProxy {
	@Test
	public void proxy() {
		final SSHConfig config = TestSSH.getConfig();
		HAssume.assumeNotNull(config);
		try (final SshClient client = SshClient.setUpDefaultClient()) {
			client.start();
			try (final ClientSession session = config.connect(client)) {
				final ExplicitPortForwardingTracker tunnel = session.createLocalPortForwardingTracker(0, new SshdSocketAddress(config.getRemote().getPort()));;

				final SSHConfig proxyConfig = new SSHConfig(new SSHRemote(config.getRemote().getUsername(), "localhost", tunnel.getBoundAddress().getPort()), config.getCredentials());
				final String message = "Test message";
				try (final SSHRunner runner = new SSHRunner(client, Duration.ofSeconds(5), proxyConfig)) {
					final String actual = new CommandProxyFactory(new DumbCommandConverter(), runner).apply(IUtils.class).echo(false, message);
					HAssert.assertEquals(message, actual.stripTrailing());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
