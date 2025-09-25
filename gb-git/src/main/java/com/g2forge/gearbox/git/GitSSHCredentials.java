package com.g2forge.gearbox.git;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.sshd.KeyPasswordProvider;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.ISupplier;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class GitSSHCredentials {
	public static TransportConfigCallback createTransportConfigCallback(ISupplier<GitSSHCredentials> supplier) {
		return new TransportConfigCallback() {
			@Override
			public void configure(Transport transport) {
				if (transport instanceof SshTransport) {
					final SshdSessionFactoryBuilder sessionFactoryBuilder = new SshdSessionFactoryBuilder();
					sessionFactoryBuilder.setHomeDirectory(FS.DETECTED.userHome());
					sessionFactoryBuilder.setSshDirectory(FS.DETECTED.userHome().toPath().resolve(HSSH.SSHDIR).toFile());
					sessionFactoryBuilder.setDefaultIdentities(unused -> HCollection.asList(supplier.get().getKey()));
					sessionFactoryBuilder.setKeyPasswordProvider(unused -> new KeyPasswordProvider() {
						@Override
						public char[] getPassphrase(URIish uri, int attempt) throws IOException {
							return supplier.get().getPassphrase().toCharArray();
						}

						@Override
						public boolean keyLoaded(URIish uri, int attempt, Exception error) throws IOException, GeneralSecurityException {
							return false;
						}

						@Override
						public void setAttempts(int maxNumberOfAttempts) {}
					});

					final SshdSessionFactory sessionFactory = sessionFactoryBuilder.build(null);

					final SshTransport sshTransport = ((SshTransport) transport);
					sshTransport.setSshSessionFactory(sessionFactory);
				}
			}
		};
	}

	protected final Path key;

	protected final String passphrase;

	public TransportConfigCallback createTransportConfigCallback() {
		return createTransportConfigCallback(() -> this);
	}
}
