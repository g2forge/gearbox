package com.g2forge.gearbox.jira.createissues;

import java.util.Set;

public interface ICreateConfig {
	public String getProject();

	public String getType();

	public String getEpic();

	public String getSecurityLevel();

	public String getAssignee();

	public Set<String> getComponents();

	public Set<String> getLabels();
}
