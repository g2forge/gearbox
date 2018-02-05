package com.g2forge.gearbox.jira;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;

import ch.qos.logback.classic.Level;

public class TestJIRAServer {
	@Test
	public void test() throws IOException, URISyntaxException, InterruptedException, ExecutionException {
		HLogback.setLogLevel(Level.INFO);
		try (final JiraRestClient client = JIRAServer.builder().host("jira.atlassian.com").build().connect(true)) {
			final String key = "JRASERVER-1";
			final Issue simple = client.getIssueClient().getIssue(key).get();
			Assert.assertEquals("Email addresses are case sensitive", simple.getSummary());
		}
	}
}
