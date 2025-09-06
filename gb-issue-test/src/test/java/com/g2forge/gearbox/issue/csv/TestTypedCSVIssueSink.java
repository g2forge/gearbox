package com.g2forge.gearbox.issue.csv;

import java.nio.file.Paths;

import org.junit.Test;

import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.gearbox.issue.DependencyNotLoadedError;

public class TestTypedCSVIssueSink {
	@Test(expected = DependencyNotLoadedError.class)
	public void loadFailure() {
		new TypedCSVIssueSink<>(Paths.get("."), ITypeRef.of(Object.class)).close();
	}
}
