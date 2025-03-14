package com.g2forge.gearbox.jira;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.event.Level;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.g2forge.alexandria.log.HLog;

public class TestJiraServer {
	@Test
	public void test() throws IOException, URISyntaxException, InterruptedException, ExecutionException {
		HLog.getLogControl().setLogLevel(Level.INFO);
		try (final JiraRestClient client = JiraAPI.builder().host("jira.atlassian.com").build().connect(true)) {
			final String key = "JRASERVER-1";
			final Issue simple = client.getIssueClient().getIssue(key).get();
			Assert.assertEquals("Email addresses are case sensitive", simple.getSummary());
		}
	}
}
