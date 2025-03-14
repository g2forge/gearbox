package com.g2forge.gearbox.jira.components;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;

import io.atlassian.util.concurrent.Promise;

public interface ProjectComponentsRestClient {
	public Promise<Iterable<BasicComponent>> getComponents(String projectKey);
}
