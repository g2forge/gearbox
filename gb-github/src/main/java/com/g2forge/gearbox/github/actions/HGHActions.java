package com.g2forge.gearbox.github.actions;

import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.g2forge.alexandria.java.core.marker.Helpers;
import com.g2forge.gearbox.github.actions.GHActionJob.GHActionJobBuilder;
import com.g2forge.gearbox.github.actions.GHActionStep.GHActionStepBuilder;
import com.g2forge.gearbox.github.actions.GHActionWorkflow.GHActionWorkflowBuilder;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
@Helpers
public class HGHActions {
	@Getter(lazy = true)
	private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().enable(Feature.MINIMIZE_QUOTES).disable(Feature.WRITE_DOC_START_MARKER));

	public static GHActionWorkflow createMavenWorkflow(String name, Set<String> dependencies) {
		if ((name == null) && (dependencies != null) && (dependencies.size() > 0)) throw new IllegalArgumentException("You must provide a name for this repository (subdirectory, logging, etc) if you want to build dependencies!");

		final GHActionWorkflowBuilder workflow = GHActionWorkflow.builder().name("Java CI with Maven");

		workflow.on(GHActionEvent.Push, GHActionEventConfiguration.builder().branch("master").build());
		workflow.on(GHActionEvent.PullRequest, GHActionEventConfiguration.builder().branch("master").build());

		final GHActionJobBuilder build = GHActionJob.builder().runsOn("ubuntu-latest");
		build.step(GHActionStep.builder().name("Set up JDK 1.8").uses("actions/setup-java@v1").with("java-version", "1.8").build());
		if (dependencies != null) for (String dependency : dependencies) {
			final String repo = dependency.substring(dependency.indexOf('/') + 1);
			build.step(GHActionStep.builder().name("Checkout " + repo).uses("actions/checkout@v2").with("repository", dependency).with("path", repo).build());
			build.step(GHActionStep.builder().name("Build " + repo).workingDirectory("./" + repo).run("mvn -B install --file pom.xml -P release -Dgpg.skip -DskipTests").build());
		}
		{
			final GHActionStepBuilder checkout = GHActionStep.builder().name("Checkout").uses("actions/checkout@v2");
			if (name != null) checkout.with("path", name);
			build.step(checkout.build());
			final GHActionStepBuilder maven = GHActionStep.builder().name("Build with Maven");
			if (name != null) maven.workingDirectory("./" + name);
			build.step(maven.run("mvn -B package --file pom.xml -P release -Dgpg.skip").build());
		}
		workflow.job("build", build.build());

		final GHActionWorkflow maven = workflow.build();
		return maven;
	}
}
