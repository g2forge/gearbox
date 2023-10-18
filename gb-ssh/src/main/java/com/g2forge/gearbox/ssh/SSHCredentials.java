package com.g2forge.gearbox.ssh;

import java.security.KeyPair;
import java.util.Collection;

import org.apache.sshd.client.ClientAuthenticationManager;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class SSHCredentials {
	@ToString.Exclude
	@Singular
	protected final Collection<? extends String> passwords;

	@Singular
	protected final Collection<? extends KeyPair> keys;

	public void configure(ClientAuthenticationManager clientAuthenticationManager) {
		if (getPasswords() != null) for (String password : getPasswords()) {
			clientAuthenticationManager.addPasswordIdentity(password);
		}
		if (getKeys() != null) for (KeyPair key : getKeys()) {
			clientAuthenticationManager.addPublicKeyIdentity(key);
		}
	}
}
