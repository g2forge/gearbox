package com.g2forge.gearbox.github.actions;

import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.g2forge.alexandria.annotations.note.Note;
import com.g2forge.alexandria.annotations.note.NoteType;
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
	private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().enable(Feature.MINIMIZE_QUOTES).disable(Feature.WRITE_DOC_START_MARKER).disable(Feature.SPLIT_LINES));

	protected static final String JAVA_VERSION = "17";

	protected static final String CHECKOUT_VERSION = "v4";

	protected static final String SETUP_JAVA_VERSION = "v3";

	@Note(type = NoteType.TODO, value = "Use gb-command to render command lines")
	public static GHActionWorkflow createMavenWorkflow(String name, String branch, String mavenSettingsXML, Set<String> dependencies, Set<String> mavenEnvSecrets) {
		final boolean hasDependencies = (dependencies != null) && (dependencies.size() > 0);
		if ((name == null) && hasDependencies) throw new IllegalArgumentException("You must provide a name for this repository (subdirectory, logging, etc) if you want to build dependencies!");

		final GHActionWorkflowBuilder workflow = GHActionWorkflow.builder().name("Java CI with Maven");

		workflow.on(GHActionEvent.Push, GHActionEventConfiguration.builder().branch(branch).build());
		workflow.on(GHActionEvent.PullRequest, GHActionEventConfiguration.builder().branch(branch).build());
		workflow.on(GHActionEvent.WorkflowDispatch, GHActionEventConfiguration.builder().build());

		final GHActionJobBuilder build = GHActionJob.builder().runsOn("ubuntu-latest").concurrency("${{ github.workflow }}-${{ github.ref }}");

		{ // Checkout
			if (hasDependencies) for (String dependency : dependencies) {
				final String repo = dependency.substring(dependency.indexOf('/') + 1);
				build.step(GHActionStep.builder().name("Checkout " + repo).uses("actions/checkout@" + CHECKOUT_VERSION).with("repository", dependency).with("path", repo).build());
			}

			final GHActionStepBuilder checkout = GHActionStep.builder().name("Checkout").uses("actions/checkout@" + CHECKOUT_VERSION);
			if (hasDependencies && (name != null)) checkout.with("path", name);
			build.step(checkout.build());
		}

		build.step(GHActionStep.builder().uses("actions/setup-java@" + SETUP_JAVA_VERSION).with("distribution", "adopt").with("java-version", JAVA_VERSION).with("cache", "maven").build());

		{
			if (hasDependencies) for (String dependency : dependencies) {
				final String repo = dependency.substring(dependency.indexOf('/') + 1);
				build.step(GHActionStep.builder().name("Build " + repo).workingDirectory("./" + repo).run("mvn -B install --file pom.xml -Prelease,release-snapshot -DskipTests").env("GITHUB_TOKEN", "${{ github.token }}").build());
			}

			final GHActionStepBuilder maven = GHActionStep.builder().name("Build");
			if (hasDependencies && (name != null)) maven.workingDirectory("./" + name);
			final String mavenGoal = hasDependencies ? "package" : "${{ (((github.event_name == 'push') || (github.event_name == 'workflow_dispatch')) && (github.ref == 'refs/heads/" + branch + "')) && 'deploy' || 'package' }}";
			maven.run("mvn" + (mavenSettingsXML == null ? "" : " -s \"" + mavenSettingsXML + "\"") + " -B " + mavenGoal + " --file pom.xml -Prelease,release-snapshot");
			maven.env("GITHUB_TOKEN", "${{ github.token }}");
			if (mavenEnvSecrets != null) for (String secret : mavenEnvSecrets) {
				maven.env(secret, "${{ secrets." + secret + " }}");
			}
			build.step(maven.build());
		}
		workflow.job("build", build.build());

		return workflow.build();
	}
}
