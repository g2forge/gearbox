package com.g2forge.gearbox.github.actions;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.resource.HResource;
import com.g2forge.alexandria.java.core.resource.Resource;
import com.g2forge.alexandria.test.HAssert;

public class TestGHAction {
	@Test
	public void readDependencies() throws JsonParseException, JsonMappingException, IOException {
		final GHActionWorkflow maven = HGHActions.getMapper().readValue(HResource.getResourceAsStream(getClass(), "maven-dependencies.yaml", true), GHActionWorkflow.class);
		HAssert.assertEquals("Java CI with Maven", maven.getName());
		HAssert.assertEquals(3, maven.getOn().size());
	}

	@Test
	public void writeDependencies() throws JsonParseException, JsonMappingException, IOException {
		final GHActionWorkflow maven = HGHActions.createMavenWorkflow("repo2", "main", null, HCollection.asSet("repo1"), null);
		HAssert.assertEquals(new Resource(getClass(), "maven-dependencies.yaml"), HGHActions.getMapper().writeValueAsString(maven).trim());
	}

	@Test
	public void writeSimple() throws JsonParseException, JsonMappingException, IOException {
		final GHActionWorkflow maven = HGHActions.createMavenWorkflow(null, "main", null, null, null);
		HAssert.assertEquals(new Resource(getClass(), "maven-simple.yaml"), HGHActions.getMapper().writeValueAsString(maven).trim());
	}
}
