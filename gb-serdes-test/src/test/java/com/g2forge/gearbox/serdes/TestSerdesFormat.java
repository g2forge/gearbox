package com.g2forge.gearbox.serdes;

import org.junit.Test;

import com.g2forge.alexandria.java.core.error.DependencyNotLoadedError;
import com.g2forge.alexandria.java.type.ref.ITypeRef;

public class TestSerdesFormat {
	@Test
	public void jsonLoadFailure() {
		SerdesFormat.JSON.createSerdesFactory(ITypeRef.of(String.class));
	}

	@Test(expected = DependencyNotLoadedError.class)
	public void xmlLoadFailure() {
		SerdesFormat.XML.createSerdesFactory(ITypeRef.of(String.class));
	}

	@Test(expected = DependencyNotLoadedError.class)
	public void yamlLoadFailure() {
		SerdesFormat.YAML.createSerdesFactory(ITypeRef.of(String.class));
	}
}
