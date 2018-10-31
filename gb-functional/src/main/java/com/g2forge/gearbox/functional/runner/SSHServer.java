package com.g2forge.gearbox.functional.runner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class SSHServer {
	protected final String username;

	protected final String password;

	protected final String host;

	protected final int port;
}
