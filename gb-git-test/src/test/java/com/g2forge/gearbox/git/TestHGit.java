package com.g2forge.gearbox.git;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.junit.Assert;
import org.junit.Test;

public class TestHGit {
	@Test
	public void isBranch() throws IOException, GitAPIException {
		try (final TempGitRepository temp = new TempGitRepository()) {
			temp.createFirstCommit();
			Assert.assertTrue(HGit.isBranch(temp.getGit(), Constants.MASTER));
			Assert.assertFalse(HGit.isBranch(temp.getGit(), "GarbageBranchName"));
		}
	}
}
