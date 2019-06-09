package com.g2forge.gearbox.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.stream.Collectors;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;

import com.g2forge.alexandria.annotations.message.TODO;
import com.g2forge.alexandria.command.Invocation;
import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.gearbox.functional.runner.IProcess;
import com.g2forge.gearbox.functional.runner.IRunner;
import com.g2forge.gearbox.functional.runner.redirect.IRedirect;

public class SSHRunner implements IRunner, ICloseable {
	protected final SshClient client;

	protected final ClientSession session;

	protected boolean open = false;

	public SSHRunner(SSHServer server) {
		client = SshClient.setUpDefaultClient();
		client.start();

		try {
			final ConnectFuture future = client.connect(server.getUsername(), server.getHost(), server.getPort());
			if (!future.await()) throw new RuntimeIOException();
			session = future.getSession();
		} catch (IOException exception) {
			throw new RuntimeIOException(exception);
		}
		session.addPasswordIdentity(server.getPassword());
		try {
			session.auth().verify();
		} catch (IOException exception) {
			throw new RuntimeIOException(exception);
		}
		open = true;
	}

	@Override
	public void close() {
		open = false;

		try {
			session.close();
		} catch (IOException exception) {
			throw new RuntimeIOException(exception);
		} finally {
			client.stop();
		}
	}

	protected void ensureOpen() {
		if (!open) throw new IllegalStateException();
	}

	@TODO("IO redirection and working directories")
	@Override
	public IProcess run(Invocation<IRedirect, IRedirect> invocation) {
		ensureOpen();
		final ChannelExec channel;
		try {
			final String command = invocation.getArguments().stream().collect(Collectors.joining(" "));
			channel = session.createExecChannel(command);
			if (!channel.open().await()) throw new RuntimeIOException();
		} catch (IOException exception) {
			throw new RuntimeIOException(exception);
		}
		return new IProcess() {
			@Override
			public void close() {
				try {
					channel.close();
				} catch (IOException e) {
					throw new RuntimeIOException(e);
				}
			}

			@Override
			public int getExitCode() {
				channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED, ClientChannelEvent.EXIT_SIGNAL, ClientChannelEvent.EXIT_STATUS), 0);
				return channel.getExitStatus();
			}

			@Override
			public InputStream getStandardError() {
				return channel.getInvertedErr();
			}

			@Override
			public OutputStream getStandardInput() {
				return channel.getInvertedIn();
			}

			@Override
			public InputStream getStandardOutput() {
				return channel.getInvertedOut();
			}
		};
	}
}
