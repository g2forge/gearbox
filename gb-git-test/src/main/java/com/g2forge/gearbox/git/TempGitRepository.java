package com.g2forge.gearbox.git;

import java.nio.file.Path;

import org.eclipse.jgit.api.Git;

import com.g2forge.alexandria.java.io.TempDirectory;

import lombok.Getter;

public class TempGitRepository extends TempDirectory {
	@Getter(lazy = true)
	private final Git git = gitToClose = HGit.createGit(getPath());

	private Git gitToClose = null;

	public TempGitRepository() {}

	public TempGitRepository(Path parent, String prefix, boolean autodelete) {
		super(parent, prefix, autodelete);
	}

	@Override
	protected void closeInternal() {
		try {
			if (gitToClose != null) gitToClose.close();
		} finally {
			super.closeInternal();
		}
	}
}
