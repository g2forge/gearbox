package com.g2forge.gearbox.jira.user;

import com.atlassian.jira.rest.client.api.UserRestClient;
import com.atlassian.jira.rest.client.api.domain.User;

import io.atlassian.util.concurrent.Promise;

public interface ExtendedUserRestClient extends UserRestClient {
	public default Promise<User> getUserByKey(final String key) {
		return getUserByQueryParam("key", key);
	}

	public Promise<User> getUserByQueryParam(final String queryParam, final String value);
}
