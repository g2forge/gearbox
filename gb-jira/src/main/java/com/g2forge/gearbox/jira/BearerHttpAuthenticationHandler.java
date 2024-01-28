package com.g2forge.gearbox.jira;

import com.atlassian.httpclient.api.Request;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class BearerHttpAuthenticationHandler implements AuthenticationHandler {
	private static final String AUTHORIZATION_HEADER = "Authorization";

	private final String token;

	@Override
	public void configure(Request.Builder builder) {
		builder.setHeader(AUTHORIZATION_HEADER, "Bearer " + getToken());
	}
}