package com.g2forge.gearbox.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.EnumSet;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;

import com.g2forge.alexandria.annotations.note.Note;
import com.g2forge.alexandria.annotations.note.NoteType;
import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.command.invocation.environment.SystemEnvironment;
import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.alexandria.java.core.error.NotYetImplementedError;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.IRunner;
import com.g2forge.gearbox.command.process.redirect.IRedirect;

public class SSHRunner implements IRunner, ICloseable {
	protected final SshClient client;

	protected final boolean ownClient;

	protected final Duration closeDuration;

	protected final ClientSession session;

	protected boolean open = false;

	public SSHRunner(Duration closeDuration, final SSHConfig config) {
		this(SshClient.setUpDefaultClient(), true, closeDuration, config);
	}

	protected SSHRunner(final SshClient client, final boolean ownClient, Duration closeDuration, final SSHConfig config) {
		Objects.requireNonNull(client);
		Objects.requireNonNull(config);

		this.client = client;
		this.ownClient = ownClient;
		this.closeDuration = closeDuration;
		if (this.ownClient) client.start();

		this.session = config.connect(client);
		open = true;
	}

	public SSHRunner(final SshClient client, Duration closeDuration, final SSHConfig config) {
		this(client, false, closeDuration, config);
	}

	@Note(type = NoteType.TODO, value = "IO redirection and working directories")
	@Note(type = NoteType.TODO, value = "Environment variables")
	@Override
	public IProcess apply(CommandInvocation<IRedirect, IRedirect> commandInvocation) {
		if ((commandInvocation.getEnvironment() != null) && !(commandInvocation.getEnvironment() instanceof SystemEnvironment)) throw new NotYetImplementedError("SSH does not yet support environment variable modifications at the process level!");

		ensureOpen();
		final ChannelExec channel;
		final Throwable launchException;
		{
			ChannelExec _channel = null;
			Throwable _launchException = null;
			try {
				final String command = commandInvocation.getArguments().stream().collect(Collectors.joining(" "));
				_channel = session.createExecChannel(command);
				if (!_channel.open().await()) throw new RuntimeIOException();
			} catch (Throwable throwable) {
				_launchException = throwable;
			}
			channel = _channel;
			launchException = _launchException;
		}

		return new IProcess() {
			@Override
			public void close() {
				if (isLaunched()) try {
					if (closeDuration == null) channel.close();
					else channel.close(false).await(closeDuration);
				} catch (IOException e) {
					throw new RuntimeIOException(e);
				}
			}

			@Override
			public int getExitCode() {
				assertLaunch();
				channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED, ClientChannelEvent.EXIT_SIGNAL, ClientChannelEvent.EXIT_STATUS), 0);
				return channel.getExitStatus();
			}

			@Override
			public Throwable getLaunchException() {
				return launchException;
			}

			@Override
			public InputStream getStandardError() {
				assertLaunch();
				return channel.getInvertedErr();
			}

			@Override
			public OutputStream getStandardInput() {
				assertLaunch();
				return channel.getInvertedIn();
			}

			@Override
			public InputStream getStandardOutput() {
				assertLaunch();
				return channel.getInvertedOut();
			}

			@Override
			public boolean isRunning() {
				return isLaunched() && channel.isOpen();
			}
		};
	}

	@Override
	public void close() {
		open = false;

		try {
			session.close();
		} catch (IOException exception) {
			throw new RuntimeIOException(exception);
		} finally {
			if (ownClient) client.stop();
		}
	}

	protected void ensureOpen() {
		if (!open) throw new IllegalStateException();
	}
}
