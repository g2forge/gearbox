package com.g2forge.gearbox.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.g2forge.gearbox.jira.components.ProjectComponentsRestClient;
import com.g2forge.gearbox.jira.sprint.SprintRestClient;
import com.g2forge.gearbox.jira.user.ExtendedUserRestClient;

public interface ExtendedJiraRestClient extends JiraRestClient {
	public ProjectComponentsRestClient getProjectComponentsClient();

	public ExtendedUserRestClient getUserClient();
	
	public SprintRestClient getSprintClient();
}
