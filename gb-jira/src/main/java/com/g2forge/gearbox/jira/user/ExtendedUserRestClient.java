package com.g2forge.gearbox.jira.user;

import com.atlassian.jira.rest.client.api.UserRestClient;
import com.atlassian.jira.rest.client.api.domain.User;

import io.atlassian.util.concurrent.Promise;

public interface ExtendedUserRestClient extends UserRestClient {
	public Promise<User> getUserByKey(final String key);
}
