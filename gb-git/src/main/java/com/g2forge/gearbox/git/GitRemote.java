package com.g2forge.gearbox.git;

import java.nio.file.Path;

import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.StoredConfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An accessor which can be used to add, delete, modify or get information about a specific git remote.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
public class GitRemote implements IGitConfigAccessor {
	protected final GitConfig config;

	@Getter(AccessLevel.PUBLIC)
	protected final String name;

	public void add(Path path, String... branches) {
		final StoredConfig config = getConfig().getConfig();
		config.setString(ConfigConstants.CONFIG_REMOTE_SECTION, name, ConfigConstants.CONFIG_KEY_URL, path.toString());
		if ((branches == null) || (branches.length < 1)) branches = new String[] { "*" };
		for (String branch : branches) {
			config.setString(ConfigConstants.CONFIG_REMOTE_SECTION, name, ConfigConstants.CONFIG_FETCH_SECTION, String.format("+refs/heads/%2$s:refs/remotes/%1$s/%2$s", name, branch));
		}
	}

	public void delete() {
		getConfig().getConfig().unsetSection(ConfigConstants.CONFIG_REMOTE_SECTION, name);
	}

	public String getURL() {
		return getConfig().getConfig().getString(ConfigConstants.CONFIG_REMOTE_SECTION, name, ConfigConstants.CONFIG_KEY_URL);
	}

	@Override
	public void save() {
		getConfig().save();
	}
}
