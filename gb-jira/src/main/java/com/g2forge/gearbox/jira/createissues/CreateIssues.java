package com.g2forge.gearbox.jira.createissues;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.wizard.PropertyStringInput;
import com.g2forge.alexandria.wizard.UserStringInput;
import com.g2forge.gearbox.jira.HLogback;
import com.g2forge.gearbox.jira.JIRAServer;
import com.g2forge.gearbox.jira.createissues.CreateIssues.Changes.ChangesBuilder;

import ch.qos.logback.classic.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

public class CreateIssues {
	@Data
	@Builder
	@AllArgsConstructor
	protected static class Changes {
		@Singular
		protected final List<CreateIssue> issues;

		@Singular
		protected final List<LinkIssuesInput> links;
	}

	protected static final Pattern PATTERN_KEY = Pattern.compile("([A-Z0-9]{2,4}-[0-9]+)(\\s.*)?");

	protected static boolean isKey(String keySummary) {
		return PATTERN_KEY.matcher(keySummary).matches();
	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, URISyntaxException, InterruptedException, ExecutionException {
		if (args.length != 1) throw new IllegalArgumentException();
		try (final InputStream stream = Files.newInputStream(Paths.get(args[0]))) {
			new CreateIssues().createIssues(stream).forEach(System.out::println);
		}
	}

	protected static void set(IConsumer1<? super String> consumer, Class<?> clazz, String property) {
		final String value = new PropertyStringInput(clazz.getSimpleName().toLowerCase() + "." + property.toLowerCase()).fallback(new UserStringInput(property, true)).get();
		if (value != null) consumer.accept(value);
	}

	protected Changes convertToREST(CreateConfig config, List<CreateIssue> issues) {
		final ChangesBuilder retVal = Changes.builder();
		for (CreateIssue issue : issues) {
			final CreateIssue fallback = issue.fallback(config);
			retVal.issue(fallback);
			for (String relationship : fallback.getRelationships().keySet()) {
				for (String target : fallback.getRelationships().get(relationship)) {
					retVal.link(new LinkIssuesInput(fallback.getSummary(), target, relationship, null));
				}
			}
		}
		return retVal.build();
	}

	public List<String> createIssues(InputStream stream) throws JsonParseException, JsonMappingException, IOException, URISyntaxException, InterruptedException, ExecutionException {
		final List<CreateIssue> file = loadIssues(stream);

		final CreateConfig.CreateConfigBuilder config = CreateConfig.builder();
		CreateIssues.set(config::project, getClass(), "Project");
		CreateIssues.set(config::type, getClass(), "Type");
		CreateIssues.set(config::epic, getClass(), "Epic");
		CreateIssues.set(s -> {
			if (!s.isEmpty()) config.labels(Stream.of(s.split(",+")).map(String::trim).collect(Collectors.toSet()));
		}, getClass(), "Labels");
		final Changes changes = convertToREST(config.build(), file);

		{ // Verify all the links we can
			final Set<String> summaries = changes.getIssues().stream().map(CreateIssue::getSummary).collect(Collectors.toSet());
			final List<String> badLinks = new ArrayList<>();
			for (LinkIssuesInput link : changes.getLinks()) {
				final String target = link.getToIssueKey();
				if (isKey(target)) continue;
				if (!summaries.contains(target)) badLinks.add(String.format("Link target \"%1$s\" <[%2$s]- \"%3$s\" is not valid!", target, link.getLinkType(), link.getFromIssueKey()));
			}
			if (!badLinks.isEmpty()) throw new IllegalArgumentException("One or more bad links:\n" + badLinks.stream().collect(Collectors.joining("\n")));
		}

		return implement(changes);
	}

	protected List<String> implement(Changes changes) throws IOException, URISyntaxException, InterruptedException, ExecutionException {
		HLogback.setLogLevel(Level.INFO);
		try (final JiraRestClient client = JIRAServer.load().connect(true)) {
			final IssueRestClient issueClient = client.getIssueClient();

			final Map<String, String> issues = new LinkedHashMap<>();
			for (CreateIssue issue : changes.getIssues()) {
				final IssueInputBuilder builder = new IssueInputBuilder(issue.getProject(), 0l);
				builder.setFieldInput(new FieldInput(IssueFieldId.ISSUE_TYPE_FIELD, ComplexIssueInputFieldValue.with("name", issue.getType())));
				if (issue.getEpic() != null) builder.setFieldInput(new FieldInput("customfield_10600", issue.getEpic()));
				if (issue.getSecurityLevel() != null) builder.setFieldInput(new FieldInput("security", ComplexIssueInputFieldValue.with("name", issue.getSecurityLevel())));
				builder.setSummary(issue.getSummary());
				builder.setDescription(issue.getDescription());
				if ((issue.getLabels() != null) && !issue.getLabels().isEmpty()) builder.setFieldInput(new FieldInput(IssueFieldId.LABELS_FIELD, issue.getLabels()));

				issues.put(issue.getSummary(), issueClient.createIssue(builder.build()).get().getKey());
			}

			for (LinkIssuesInput link : changes.getLinks()) {
				final String from = issues.get(link.getFromIssueKey());
				final String to = issues.getOrDefault(link.getToIssueKey(), link.getToIssueKey());
				issueClient.linkIssue(new LinkIssuesInput(from, to, link.getLinkType(), link.getComment())).get();
			}

			return new ArrayList<>(issues.values());
		}
	}

	protected List<CreateIssue> loadIssues(final InputStream stream) throws IOException, JsonParseException, JsonMappingException {
		final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		return mapper.readValue(stream, new TypeReference<List<CreateIssue>>() {});
	}
}
