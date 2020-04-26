package com.g2forge.gearbox.github.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.g2forge.alexandria.java.core.marker.Helpers;
import com.g2forge.gearbox.github.actions.GHActionJob.GHActionJobBuilder;
import com.g2forge.gearbox.github.actions.GHActionWorkflow.GHActionWorkflowBuilder;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
@Helpers
public class HGHActions {
	@Getter(lazy = true)
	private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().enable(Feature.MINIMIZE_QUOTES).disable(Feature.WRITE_DOC_START_MARKER));

	public static GHActionWorkflow createMavenWorkflow() {
		final GHActionWorkflowBuilder workflow = GHActionWorkflow.builder().name("Java CI with Maven");

		workflow.on(GHActionEvent.Push, GHActionEventConfiguration.builder().branch("master").build());
		workflow.on(GHActionEvent.PullRequest, GHActionEventConfiguration.builder().branch("master").build());

		final GHActionJobBuilder build = GHActionJob.builder().runsOn("ubuntu-latest");
		build.step(GHActionStep.builder().uses("actions/checkout@v2").build());
		build.step(GHActionStep.builder().name("Set up JDK 1.8").uses("actions/setup-java@v1").with("java-version", "1.8").build());
		build.step(GHActionStep.builder().name("Build with Maven").run("mvn -B package --file pom.xml -P release").build());
		workflow.job("build", build.build());

		final GHActionWorkflow maven = workflow.build();
		return maven;
	}
}
