package com.g2forge.gearbox.git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Comparator;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.CheckoutEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.ReflogEntry;

import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.marker.Helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@Helpers
@UtilityClass
public class HGit {
	public static ZonedDateTime getTime(ReflogEntry entry) {
		final PersonIdent who = entry.getWho();
		return ZonedDateTime.ofInstant(who.getWhen().toInstant(), who.getTimeZone().toZoneId());
	}

	public static class ReflogTimeComparator implements Comparator<ReflogEntry> {
		@Override
		public int compare(ReflogEntry arg0, ReflogEntry arg1) {
			return HGit.getTime(arg1).compareTo(HGit.getTime(arg0));
		}
	}

	@AllArgsConstructor
	public static class WhoReflogEntry implements ReflogEntry {
		@Getter
		protected final PersonIdent who;

		@Override
		public String getComment() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectId getNewId() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectId getOldId() {
			throw new UnsupportedOperationException();
		}

		@Override
		public CheckoutEntry parseCheckout() {
			throw new UnsupportedOperationException();
		}
	}

	public static Git createGit(Path root) {
		return createGit(root, true);
	}

	public static Git createGit(Path root, boolean create) {
		try {
			final Path gitDir = getGitFile(root);
			final boolean actuallyCreate = create && !Files.isDirectory(gitDir);
			if (actuallyCreate) Files.createDirectories(root);
			final FileRepository repository = new FileRepository(gitDir.toFile());
			if (actuallyCreate) repository.create();

			final Git git = new Git(repository);
			if (create) {
				if (!Files.isDirectory(gitDir)) try {
					Git.init().setGitDir(gitDir.toFile()).call();
				} catch (GitAPIException exception) {
					throw new RuntimeException("Failed to initialize new git repository in " + root, exception);
				}
			}
			return git;
		} catch (IOException exception) {
			throw new RuntimeIOException(exception);
		}
	}

	public static Path getGitFile(Path root) {
		return root.resolve(".git");
	}

	public static final String GIT_DIRECTORY = ".git";
}
