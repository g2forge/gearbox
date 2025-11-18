package com.g2forge.gearbox.jira;

import java.net.URI;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.g2forge.gearbox.jira.components.AsynchronousProjectComponentClient;
import com.g2forge.gearbox.jira.components.ProjectComponentsRestClient;
import com.g2forge.gearbox.jira.sprint.AsynchronousSprintRestClient;
import com.g2forge.gearbox.jira.sprint.SprintRestClient;
import com.g2forge.gearbox.jira.user.AsynchronousExtendedUserRestClient;
import com.g2forge.gearbox.jira.user.ExtendedUserRestClient;

import jakarta.ws.rs.core.UriBuilder;
import lombok.Getter;

@Getter
public class ExtendedAsynchronousJiraRestClient extends AsynchronousJiraRestClient implements ExtendedJiraRestClient {
	protected final ProjectComponentsRestClient projectComponentsClient;

	protected final ExtendedUserRestClient userClient;

	protected final SprintRestClient sprintClient;

	public ExtendedAsynchronousJiraRestClient(URI serverUri, DisposableHttpClient httpClient) {
		super(serverUri, httpClient);

		{
			final URI standardBaseURI = UriBuilder.fromUri(serverUri).path("/rest/api/latest").build();
			projectComponentsClient = new AsynchronousProjectComponentClient(standardBaseURI, httpClient);
			userClient = new AsynchronousExtendedUserRestClient(standardBaseURI, httpClient);
		}

		{
			final URI agileBaseURI = UriBuilder.fromUri(serverUri).path("/rest/agile/1.0").build();
			sprintClient = new AsynchronousSprintRestClient(agileBaseURI, httpClient);
		}
	}
}
