package com.g2forge.gearbox.git;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.jgit.lib.Constants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
@AllArgsConstructor
public class GitIgnore {
	public static GitIgnore load(Path directory) throws IOException {
		try (final BufferedReader reader = Files.newBufferedReader(directory.resolve(Constants.GITIGNORE_FILENAME))) {
			final GitIgnore.GitIgnoreBuilder retVal = GitIgnore.builder();
			while (true) {
				final String line = reader.readLine();
				if (line == null) break;
				retVal.line(line);
			}
			return retVal.build();
		}
	}

	@Singular
	protected final List<String> lines;

	public void store(Path directory) throws IOException {
		try (final PrintStream output = new PrintStream(Files.newOutputStream(directory.resolve(Constants.GITIGNORE_FILENAME)))) {
			for (String line : getLines()) {
				output.println(line);
			}
		}
	}
}
