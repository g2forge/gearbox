package com.g2forge.gearbox.jira.createissues;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
@AllArgsConstructor
public class CreateConfig implements ICreateConfig {
	protected final String project;

	protected final String type;

	protected final String epic;

	protected final String securityLevel;

	@Singular
	protected final Set<String> components;

	@Singular
	protected final Set<String> labels;
}
