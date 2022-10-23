package com.g2forge.gearbox.jira;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;

import io.atlassian.util.concurrent.Promise;

public class AsynchronousProjectComponentClient extends AbstractAsynchronousRestClient implements ProjectComponentsRestClient {
	private static final String PROJECT_URI_PREFIX = "project";

	private static final String COMPONENTS_URI_SUFFIX = "components";

	private final BasicComponentsJsonParser basicComponentsJsonParser = new BasicComponentsJsonParser();

	private final URI baseUri;

	public AsynchronousProjectComponentClient(final URI baseUri, final HttpClient client) {
		super(client);
		this.baseUri = baseUri;
	}

	@Override
	public Promise<Iterable<BasicComponent>> getComponents(String projectKey) {
		final URI uri = UriBuilder.fromUri(baseUri).path(PROJECT_URI_PREFIX).path(projectKey).path(COMPONENTS_URI_SUFFIX).build();
		return getAndParse(uri, basicComponentsJsonParser);
	}
}
