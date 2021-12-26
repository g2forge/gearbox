package com.g2forge.gearbox.maven;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.maven.ProcessOutputHandler.IOutputMatcher;

public enum MavenDownloadErrors {
	MISSING_ARTIFACT {
		@Override
		public IOutputMatcher createMatcher() {
			return new IOutputMatcher() {
				protected boolean matched;

				@Override
				public boolean isApplicable(boolean success) {
					return !success;
				}

				@Override
				public Boolean isMatch(String line, boolean output) {
					if (matched) return true;
					matched = line.startsWith("[ERROR]") && PATTERN_MISSINGARTIFACT.matcher(line).find();
					return matched ? true : null;
				}
			};
		}
	};

	public static final Pattern PATTERN_MISSINGARTIFACT = Pattern.compile("Failed to execute goal org\\.apache\\.maven\\.plugins:maven-dependency-plugin:([0-9]+(\\.[0-9]+)*):copy \\(default-cli\\) on project standalone-pom: Unable to find artifact\\.:");

	public static Set<MavenDownloadErrors> process(Logger log, IProcess process) {
		return new ProcessOutputHandler<>(log, EnumSet.allOf(MavenDownloadErrors.class), MavenDownloadErrors::createMatcher).handle(process);
	}

	public abstract ProcessOutputHandler.IOutputMatcher createMatcher();
}