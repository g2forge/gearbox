package com.g2forge.gearbox.github.codeowners;

import org.junit.Test;

import com.g2forge.alexandria.java.core.resource.Resource;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.github.codeowners.GHCodeOwners.GHCodeOwnersBuilder;
import com.g2forge.gearbox.github.codeowners.convert.GHCORenderer;

public class TestGHCodeOwners {
	@Test
	public void render() {
		final GHCodeOwnersBuilder builder = GHCodeOwners.builder();
		builder.line(new GHCOComment("This is a comment."));
		builder.line(GHCOBlank.create());
		builder.line(GHCOPattern.builder().pattern("*").owner("@global-owner1").owner("@global-owner2").build());
		builder.line(new GHCOPattern("*.js", "js-owner@example.com"));
		final GHCodeOwners codeowners = builder.build();
		final String actual = new GHCORenderer().render(codeowners);

		HAssert.assertEquals(new Resource(getClass(), "CODEOWNERS"), actual);
	}
}
