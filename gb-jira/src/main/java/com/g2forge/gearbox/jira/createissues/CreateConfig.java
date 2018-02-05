package com.g2forge.gearbox.jira.createissues;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateConfig implements ICreateConfig {
	protected final String project;

	protected final String type;

	protected final String epic;
}
