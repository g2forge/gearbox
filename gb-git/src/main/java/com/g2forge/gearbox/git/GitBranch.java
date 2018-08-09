package com.g2forge.gearbox.git;

import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.StoredConfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An accessor which can be used to add, delete, modify or get information about a specific git branch.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
public class GitBranch implements IGitConfigAccessor {
	protected final GitConfig config;

	@Getter(AccessLevel.PUBLIC)
	protected final String name;

	public GitBranch delete() {
		getConfig().getConfig().unsetSection(ConfigConstants.CONFIG_BRANCH_SECTION, getName());
		return this;
	}

	public String getRemote() {
		return getConfig().getConfig().getString(ConfigConstants.CONFIG_BRANCH_SECTION, getName(), ConfigConstants.CONFIG_KEY_REMOTE);
	}

	public String getTracking() {
		return getConfig().getConfig().getString(ConfigConstants.CONFIG_BRANCH_SECTION, getName(), ConfigConstants.CONFIG_KEY_MERGE);
	}

	@Override
	public void save() {
		getConfig().save();
	}

	public GitBranch setTracking(String remote, String tracking) {
		final StoredConfig config = getConfig().getConfig();
		config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, getName(), ConfigConstants.CONFIG_KEY_REMOTE, remote);
		config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, getName(), ConfigConstants.CONFIG_KEY_MERGE, Constants.R_HEADS + (tracking == null ? getName() : tracking));
		return this;
	}
}
