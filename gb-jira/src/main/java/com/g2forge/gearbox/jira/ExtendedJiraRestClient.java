package com.g2forge.gearbox.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.g2forge.gearbox.jira.components.ProjectComponentsRestClient;

public interface ExtendedJiraRestClient extends JiraRestClient {
	public ProjectComponentsRestClient getProjectComponentsClient();
}
