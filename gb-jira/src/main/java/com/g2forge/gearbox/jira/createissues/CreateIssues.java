package com.g2forge.gearbox.jira.createissues;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
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

import org.slf4j.event.Level;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.atlassian.util.concurrent.Promise;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.g2forge.alexandria.command.IStandardCommand;
import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.command.exit.IExit;
import com.g2forge.alexandria.java.core.error.HError;
import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.log.HLog;
import com.g2forge.alexandria.wizard.PropertyStringInput;
import com.g2forge.alexandria.wizard.UserStringInput;
import com.g2forge.gearbox.jira.JIRAServer;

public class CreateIssues implements IStandardCommand {
	protected static final Pattern PATTERN_KEY = Pattern.compile("([A-Z0-9]{2,4}-[0-9]+)(\\s.*)?");

	protected static boolean isKey(String keySummary) {
		return PATTERN_KEY.matcher(keySummary).matches();
	}

	public static void main(String[] args) throws Throwable {
		IStandardCommand.main(args, new CreateIssues());
	}

	protected static void set(IConsumer1<? super String> consumer, Class<?> clazz, String property) {
		final String value = new PropertyStringInput(clazz.getSimpleName().toLowerCase() + "." + property.toLowerCase()).fallback(new UserStringInput(property, true)).get();
		if (value != null) consumer.accept(value);
	}

	protected Changes convertToREST(CreateConfig config, List<CreateIssue> issues) {
		final Changes.ChangesBuilder retVal = Changes.builder();
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
		HLog.getLogControl().setLogLevel(Level.INFO);
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

				final List<Throwable> throwables = new ArrayList<>();
				for (int i = 0; i < 5; i++) {
					final Promise<BasicIssue> promise = issueClient.createIssue(builder.build());
					final BasicIssue created;
					try {
						created = promise.get();
					} catch (ExecutionException e) {
						throwables.add(e);
						continue;
					}
					issues.put(issue.getSummary(), created.getKey());
					throwables.clear();
					break;
				}
				if (!throwables.isEmpty()) {
					HError.multithrow(String.format("Failed to create issue: %1$s", issue.getSummary()), throwables).printStackTrace(System.err);
				}
			}

			for (LinkIssuesInput link : changes.getLinks()) {
				final String from = issues.get(link.getFromIssueKey());
				final String to = issues.getOrDefault(link.getToIssueKey(), link.getToIssueKey());
				// TODO: Handle it when an issue we're linking wasn't created
				issueClient.linkIssue(new LinkIssuesInput(from, to, link.getLinkType(), link.getComment())).get();
			}

			return new ArrayList<>(issues.values());
		}
	}

	protected List<CreateIssue> loadIssues(final InputStream stream) throws IOException, JsonParseException, JsonMappingException {
		final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		return mapper.readValue(stream, new TypeReference<List<CreateIssue>>() {});
	}

	@Override
	public IExit invoke(CommandInvocation<InputStream, PrintStream> invocation) throws Throwable {
		if (invocation.getArguments().size() != 1) throw new IllegalArgumentException();
		try (final InputStream stream = Files.newInputStream(Paths.get(invocation.getArguments().get(0)))) {
			createIssues(stream).forEach(System.out::println);
		}
		return SUCCESS;
	}
}
