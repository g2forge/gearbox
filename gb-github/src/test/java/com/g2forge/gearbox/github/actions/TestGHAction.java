package com.g2forge.gearbox.github.actions;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.g2forge.alexandria.java.core.resource.HResource;
import com.g2forge.alexandria.java.core.resource.Resource;
import com.g2forge.alexandria.test.HAssert;

public class TestGHAction {
	@Test
	public void read() throws JsonParseException, JsonMappingException, IOException {
		final GHActionWorkflow maven = HGHActions.getMapper().readValue(HResource.getResourceAsStream(getClass(), "maven.yaml", true), GHActionWorkflow.class);
		HAssert.assertEquals("Java CI with Maven", maven.getName());
		HAssert.assertEquals(2, maven.getOn().size());
	}

	@Test
	public void write() throws JsonParseException, JsonMappingException, IOException {
		final GHActionWorkflow maven = HGHActions.createMavenWorkflow(null, null);
		HAssert.assertEquals(new Resource(getClass(), "maven.yaml"), HGHActions.getMapper().writeValueAsString(maven).trim());
	}
}
