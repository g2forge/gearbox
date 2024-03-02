package com.g2forge.gearbox.ssh;

import java.io.IOException;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;

import com.g2forge.alexandria.java.io.RuntimeIOException;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class SSHRemote {
	protected final String username;

	protected final String host;

	protected final int port;

	public ClientSession connect(SshClient client) {
		try {
			final ConnectFuture future = client.connect(getUsername(), getHost(), getPort());
			if (!future.await()) throw new RuntimeIOException("Failed to connect to " + this);
			return future.verify().getSession();
		} catch (IOException exception) {
			throw new RuntimeIOException("Failed to connect to " + this, exception);
		}
	}
}
