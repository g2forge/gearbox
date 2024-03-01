package com.g2forge.gearbox.ssh;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.util.Collection;
import java.util.Collections;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.fs.SftpFileSystemClientSessionInitializer;
import org.apache.sshd.sftp.client.fs.SftpFileSystemInitializationContext;
import org.apache.sshd.sftp.client.fs.SftpFileSystemProvider;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.io.RuntimeIOException;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class SSHConfig {
	protected final SSHRemote remote;

	protected final SSHCredentials credentials;

	public ClientSession connect(SshClient client) {
		final ClientSession retVal = getRemote().connect(client);
		if (getCredentials() != null) getCredentials().configure(retVal);

		try {
			retVal.auth().verify();
		} catch (IOException exception) {
			throw new RuntimeIOException(exception);
		}
		return retVal;
	}

	public FileSystem createFileSystem() {
		return createFileSystem(null /* This works because SftpFileSystemProvider does this anyway for the no-arg constructor */);
	}

	public FileSystem createFileSystem(SshClient client) {
		final SftpFileSystemProvider provider = new SftpFileSystemProvider(client);
		provider.setSftpFileSystemClientSessionInitializer(new SftpFileSystemClientSessionInitializer() {
			@Override
			public void authenticateClientSession(SftpFileSystemProvider provider, SftpFileSystemInitializationContext context, ClientSession session) throws IOException {
				final String password = context.getCredentials().getPassword();
				// If no password provided perhaps the client is set-up to use registered public keys
				if (password != null) session.addPasswordIdentity(password);
				getCredentials().configure(session);
				session.auth().verify(context.getMaxAuthTime());
			}
		});

		final SSHRemote remote = getRemote();
		final Collection<? extends String> passwords = getCredentials().getPasswords();
		final String password = passwords.isEmpty() ? null : HCollection.getFirst(passwords);
		final URI uri = SftpFileSystemProvider.createFileSystemURI(remote.getHost(), remote.getPort(), remote.getUsername(), password);

		try {
			return provider.newFileSystem(uri, Collections.<String, Object>emptyMap());
		} catch (IOException e) {
			throw new RuntimeIOException("Failed to create SFTP filesystem for " + this, e);
		}
	}
}
