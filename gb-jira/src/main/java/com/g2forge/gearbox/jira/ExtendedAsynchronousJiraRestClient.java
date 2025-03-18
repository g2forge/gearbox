package com.g2forge.gearbox.jira;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.g2forge.gearbox.jira.components.AsynchronousProjectComponentClient;
import com.g2forge.gearbox.jira.components.ProjectComponentsRestClient;
import com.g2forge.gearbox.jira.user.AsynchronousExtendedUserRestClient;
import com.g2forge.gearbox.jira.user.ExtendedUserRestClient;

import lombok.Getter;

public class ExtendedAsynchronousJiraRestClient extends AsynchronousJiraRestClient implements ExtendedJiraRestClient {
	@Getter
	protected final ProjectComponentsRestClient projectComponentsClient;

	@Getter
	protected final ExtendedUserRestClient userClient;

	public ExtendedAsynchronousJiraRestClient(URI serverUri, DisposableHttpClient httpClient) {
		super(serverUri, httpClient);
		final URI baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/latest").build();
		projectComponentsClient = new AsynchronousProjectComponentClient(baseUri, httpClient);
		userClient = new AsynchronousExtendedUserRestClient(baseUri, httpClient);
	}
}
