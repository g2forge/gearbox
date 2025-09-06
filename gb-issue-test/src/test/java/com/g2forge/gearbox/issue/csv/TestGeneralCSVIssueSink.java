package com.g2forge.gearbox.issue.csv;

import java.nio.file.Paths;

import org.junit.Test;

import com.g2forge.gearbox.issue.DependencyNotLoadedError;

public class TestGeneralCSVIssueSink {
	@Test(expected = DependencyNotLoadedError.class)
	public void loadFailure() {
		new GeneralCSVIssueSink<>(Paths.get(".")).close();
	}
}
