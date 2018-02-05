package com.g2forge.gearbox.jira.createissues;

import java.util.List;

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

	protected final String summary;

	protected final String description;

	@Singular("related")
	protected final List<String> related;

	public CreateIssue fallback(CreateConfig config) {
		final CreateIssueBuilder retVal = builder();
		retVal.project(IFunction1.create(ICreateConfig::getProject).applyWithFallback(this, config));
		retVal.type(IFunction1.create(ICreateConfig::getType).applyWithFallback(this, config));
		retVal.epic(IFunction1.create(ICreateConfig::getEpic).applyWithFallback(this, config));
		retVal.summary(getSummary());
		retVal.description(getDescription());
		retVal.related(getRelated());
		return retVal.build();
	}
}
