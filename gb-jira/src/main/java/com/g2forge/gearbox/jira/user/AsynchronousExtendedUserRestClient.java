package com.g2forge.gearbox.jira.user;

import java.net.URI;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousUserRestClient;

import io.atlassian.util.concurrent.Promise;
import jakarta.ws.rs.core.UriBuilder;

public class AsynchronousExtendedUserRestClient extends AsynchronousUserRestClient implements ExtendedUserRestClient {
	protected static final String USER_URI_PREFIX = "user";

	protected final URI baseUri;

	public AsynchronousExtendedUserRestClient(URI baseUri, HttpClient client) {
		super(baseUri, client);
		this.baseUri = baseUri;
	}

	public Promise<User> getUserByQueryParam(final String queryParam, final String value) {
		final URI userUri = UriBuilder.fromUri(baseUri).path(USER_URI_PREFIX).queryParam(queryParam, value).queryParam("expand", "groups").build();
		return getUser(userUri);
	}
}
