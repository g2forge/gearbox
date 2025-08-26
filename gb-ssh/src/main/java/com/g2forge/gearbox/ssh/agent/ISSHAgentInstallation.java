package com.g2forge.gearbox.ssh.agent;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface ISSHAgentInstallation {
	public static class System implements ISSHAgentInstallation {
		@Override
		public Path getSSHAddExecutable() {
			return Paths.get(ISSHAgent.SSH_ADD);
		}

		@Override
		public Path getSSHAgentExecutable() {
			return Paths.get(ISSHAgent.SSH_AGENT);
		}

		@Override
		public String translateSocket(String socket) {
			return socket;
		}
	}

	public Path getSSHAddExecutable();

	public Path getSSHAgentExecutable();

	public String translateSocket(String socket);
}
