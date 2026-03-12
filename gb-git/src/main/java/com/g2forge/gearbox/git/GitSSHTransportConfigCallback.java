package com.g2forge.gearbox.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

import org.eclipse.jgit.annotations.NonNull;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.internal.transport.ssh.OpenSshConfigFile;
import org.eclipse.jgit.transport.SshConfigStore;
import org.eclipse.jgit.transport.SshConstants;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.sshd.KeyPasswordProvider;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder.ConfigStoreFactory;
import org.eclipse.jgit.util.FS;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.ISupplier;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class GitSSHTransportConfigCallback implements TransportConfigCallback {
	public enum StrictHostKeyChecking {
		ASK,
		YES,
		ACCEPT_NEW,
		NO;

		public String getValue() {
			return name().toLowerCase().replace("_", "-");
		}
	}

	protected final ISupplier<GitSSHCredentials> credentialSupplier;

	protected final StrictHostKeyChecking strictHostKeyChecking;

	protected final Path homeDirectory;

	protected final Path sshDirectory;

	@Override
	public void configure(Transport transport) {
		if (transport instanceof SshTransport) {
			final SshdSessionFactoryBuilder sessionFactoryBuilder = new SshdSessionFactoryBuilder();
			sessionFactoryBuilder.setHomeDirectory(getHomeDirectory() == null ? FS.DETECTED.userHome() : getHomeDirectory().toFile());
			sessionFactoryBuilder.setSshDirectory(getSshDirectory() == null ? FS.DETECTED.userHome().toPath().resolve(HSSH.SSHDIR).toFile() : getSshDirectory().toFile());
			sessionFactoryBuilder.setDefaultIdentities(unused -> HCollection.asList(getCredentialSupplier().get().getKey()));
			if (getStrictHostKeyChecking() != null) sessionFactoryBuilder.setConfigStoreFactory(new ConfigStoreFactory() {
				@Override
				public SshConfigStore create(File homeDir, File configFile, String localUserName) {
					return new OpenSshConfigFile(homeDir, configFile, localUserName) {
						@Override
						public OpenSshConfigFile.HostEntry lookup(@NonNull String hostName, int port, String userName) {
							final HostEntry retVal = super.lookup(hostName, port, localUserName);
							retVal.setValue(SshConstants.STRICT_HOST_KEY_CHECKING, getStrictHostKeyChecking().getValue());
							return retVal;
						}
					};
				}
			});
			sessionFactoryBuilder.setKeyPasswordProvider(unused -> new KeyPasswordProvider() {
				@Override
				public char[] getPassphrase(URIish uri, int attempt) throws IOException {
					return getCredentialSupplier().get().getPassphrase().toCharArray();
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
}