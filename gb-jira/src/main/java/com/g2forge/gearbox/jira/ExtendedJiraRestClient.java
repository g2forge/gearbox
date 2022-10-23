package com.g2forge.gearbox.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;

public interface ExtendedJiraRestClient extends JiraRestClient {
	public ProjectComponentsRestClient getProjectComponentsClient();
}
