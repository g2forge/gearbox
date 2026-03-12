package com.g2forge.gearbox.git;

import java.nio.file.Path;

import org.eclipse.jgit.api.TransportConfigCallback;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class GitSSHCredentials {
	protected final Path key;

	protected final String passphrase;

	public TransportConfigCallback createTransportConfigCallback() {
		return GitSSHTransportConfigCallback.builder().credentialSupplier(() -> this).build();
	}
}
