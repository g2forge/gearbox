package com.g2forge.gearbox.jira.createissues;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.g2forge.alexandria.java.function.IFunction1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
@AllArgsConstructor
public class CreateIssue implements ICreateConfig {
	protected final String project;

	protected final String type;

	protected final String epic;

	protected final String securityLevel;

	protected final String summary;

	protected final String description;

	@Singular
	protected final Set<String> components;

	@Singular
	protected final Set<String> labels;

	@Singular
	protected final Map<String, List<String>> relationships;

	public CreateIssue fallback(CreateConfig config) {
		final CreateIssueBuilder retVal = builder();

		retVal.project(IFunction1.create(ICreateConfig::getProject).applyWithFallback(this, config));
		retVal.type(IFunction1.create(ICreateConfig::getType).applyWithFallback(this, config));
		retVal.epic(IFunction1.create(ICreateConfig::getEpic).applyWithFallback(this, config));
		retVal.components(Stream.of(this, config).map(ICreateConfig::getComponents).flatMap(l -> l == null ? Stream.empty() : l.stream()).collect(Collectors.toSet()));
		retVal.labels(Stream.of(this, config).map(ICreateConfig::getLabels).flatMap(l -> l == null ? Stream.empty() : l.stream()).collect(Collectors.toSet()));

		retVal.securityLevel(getSecurityLevel());
		retVal.summary(getSummary());
		retVal.description(getDescription());
		if (getRelationships() != null) retVal.relationships(getRelationships());

		return retVal.build();
	}
}
