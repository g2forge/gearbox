package com.g2forge.gearbox.ssh.agent;

import java.nio.file.Path;

import com.g2forge.alexandria.java.close.ICloseable;

public interface ISSHAgent extends ICloseable {
	public static final String SSH_AUTH_SOCK = "SSH_AUTH_SOCK";

	public static final String SSH_AGENT_PID = "SSH_AGENT_PID";

	public static final String SSH_AGENT = "ssh-agent";

	public static final String SSH_ADD = "ssh-add";

	public String getSocket();

	public boolean add(Path key, String passphrase);
}
