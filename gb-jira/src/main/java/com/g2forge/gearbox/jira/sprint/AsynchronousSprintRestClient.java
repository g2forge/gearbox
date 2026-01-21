package com.g2forge.gearbox.jira.sprint;

import java.net.URI;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import com.g2forge.gearbox.jira.sprint.Sprint.SprintBuilder;

import io.atlassian.util.concurrent.Promise;
import jakarta.ws.rs.core.UriBuilder;

public class AsynchronousSprintRestClient extends AbstractAsynchronousRestClient implements SprintRestClient {
	protected static class SprintJsonParser implements JsonObjectParser<Sprint> {
		@Override
		public Sprint parse(JSONObject json) throws JSONException {
			final SprintBuilder retVal = Sprint.builder();
			retVal.self(JsonParseUtil.getSelfUri(json));
			retVal.id(json.getLong("id"));
			retVal.name(json.getString("name"));
			retVal.startDate(JsonParseUtil.parseDateTime(json.getString("startDate")));
			retVal.endDate(JsonParseUtil.parseDateTime(json.getString("endDate")));
			return retVal.build();
		}
	}

	protected class SprintJsonGenerator implements JsonGenerator<Sprint> {
		@Override
		public JSONObject generate(final Sprint sprint) throws JSONException {
			final JSONObject retVal = new JSONObject();
			if (sprint.getId() != null) retVal.putOpt("id", sprint.getId());
			if (sprint.getName() != null) retVal.putOpt("name", sprint.getName());
			if (sprint.getStartDate() != null) retVal.putOpt("startDate", JsonParseUtil.formatDateTime(sprint.getStartDate()));
			if (sprint.getEndDate() != null) retVal.putOpt("endDate", JsonParseUtil.formatDateTime(sprint.getEndDate()));
			return retVal;
		}
	}

	protected final URI baseUri;

	public AsynchronousSprintRestClient(URI baseUri, HttpClient client) {
		super(client);
		this.baseUri = baseUri;
	}

	protected final SprintJsonParser PARSER = new SprintJsonParser();

	protected final SprintJsonGenerator GENERATOR = new SprintJsonGenerator();

	@Override
	public Promise<Sprint> getSprintById(final long id) {
		return getAndParse(createSprintURI(id), PARSER);
	}

	protected URI createSprintURI(final long id) {
		return UriBuilder.fromUri(baseUri).path("sprint").path(Long.toString(id)).build();
	}

	@Override
	public Promise<Sprint> updateSprint(Sprint sprint) {
		return postAndParse(createSprintURI(sprint.getId()), sprint, GENERATOR, PARSER);
	}
}
