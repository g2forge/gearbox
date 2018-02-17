package com.g2forge.gearbox.git.ssh;

import com.jcraft.jsch.UserInfo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PasswordSSHUserInfo implements UserInfo {
	protected final String password;

	@Override
	public String getPassphrase() {
		return password;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean promptPassphrase(String arg0) {
		return true;
	}

	@Override
	public boolean promptPassword(String arg0) {
		return true;
	}

	@Override
	public boolean promptYesNo(String arg0) {
		return true;
	}

	@Override
	public void showMessage(String arg0) {
		throw new UnsupportedOperationException();
	}
}
