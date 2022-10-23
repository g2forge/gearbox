package com.g2forge.gearbox.jira;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.internal.json.BasicComponentJsonParser;
import com.atlassian.jira.rest.client.internal.json.JsonArrayParser;

public class BasicComponentsJsonParser implements JsonArrayParser<Iterable<BasicComponent>> {
	private final BasicComponentJsonParser basicComponentJsonParser = new BasicComponentJsonParser();

	@Override
	public Iterable<BasicComponent> parse(JSONArray json) throws JSONException {
		final List<BasicComponent> retVal = new ArrayList<>(json.length());
		for (int i = 0; i < json.length(); i++) {
			retVal.add(basicComponentJsonParser.parse(json.getJSONObject(i)));

		}
		return retVal;
	}
}