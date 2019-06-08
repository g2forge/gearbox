package com.g2forge.gearbox.git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.g2forge.alexandria.java.io.file.TempDirectory;

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

	public void createFirstCommit() {
		final String file = "file";
		try {
			Files.createFile(getPath().resolve(file));
			getGit().add().addFilepattern(file).call();
			getGit().commit().setMessage(file).call();
		} catch (IOException | GitAPIException e) {
			throw new RuntimeException(e);
		}
	}
}
