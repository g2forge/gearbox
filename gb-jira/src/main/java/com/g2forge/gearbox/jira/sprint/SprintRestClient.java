package com.g2forge.gearbox.jira.sprint;

import io.atlassian.util.concurrent.Promise;

public interface SprintRestClient {
	public Promise<Sprint> getSprintById(final long id);

	public Promise<Sprint> updateSprint(Sprint sprint);
}
