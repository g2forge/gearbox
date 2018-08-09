package com.g2forge.gearbox.git;

import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.StoredConfig;

import com.g2forge.alexandria.java.io.RuntimeIOException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Accessor for git repository configuration, including {@link GitRemote remotes}.
 */
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class GitConfig implements IGitConfigAccessor {
	protected final StoredConfig config;

	public GitConfig(Git git) {
		this(git.getRepository().getConfig());
	}

	/**
	 * Get access to a branch with the specific name.
	 * 
	 * @param name The name of the branch.
	 * @return An accessor which can be used to configure the specified branch.
	 */
	public GitBranch getBranch(String name) {
		return new GitBranch(this, name);
	}

	public GitRemote getOrigin() {
		return getRemote(Constants.DEFAULT_REMOTE_NAME);
	}

	/**
	 * Get access to a remote with the specified name, which may or may not already be configured.
	 * 
	 * @param name The name of the remote.
	 * @return An accessor which can be used to add, delete, modify or get information about the remote.
	 */
	public GitRemote getRemote(String name) {
		return new GitRemote(this, name);
	}

	@Override
	public void save() {
		try {
			getConfig().save();
		} catch (IOException exception) {
			throw new RuntimeIOException("Failed to save git repository configuration!", exception);
		}
	}
}
