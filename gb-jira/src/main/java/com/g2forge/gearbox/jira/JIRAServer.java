package com.g2forge.gearbox.jira;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.httpclient.apache.httpcomponents.DefaultHttpClientFactory;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.factory.HttpClientOptions;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.AtlassianHttpClientDecorator;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import com.g2forge.alexandria.java.fluent.optional.NullableOptional;
import com.g2forge.alexandria.wizard.PropertyStringInput;
import com.g2forge.alexandria.wizard.UserPasswordInput;
import com.g2forge.alexandria.wizard.UserStringInput;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JIRAServer {
	public static DisposableHttpClient createClient(final URI uri, final AuthenticationHandler authenticationHandler, HttpClientOptions options) {
		final EventPublisher eventPublisher = new EventPublisher() {
			@Override
			public void publish(Object arg0) {}

			@Override
			public void register(Object arg0) {}

			@Override
			public void unregister(Object arg0) {}

			@Override
			public void unregisterAll() {}
		};
		final ApplicationProperties applicationProperties = new ApplicationProperties() {
			@Override
			public String getBaseUrl() {
				return uri.getPath();
			}

			@Override
			public String getBaseUrl(UrlMode arg0) {
				return uri.getPath();
			}

			@Override
			public Date getBuildDate() {
				throw new UnsupportedOperationException();
			}

			@Override
			public String getBuildNumber() {
				return null;
			}

			@Override
			public String getDisplayName() {
				return null;
			}

			@Override
			public File getHomeDirectory() {
				throw new UnsupportedOperationException();
			}

			@Override
			public String getPlatformId() {
				throw new UnsupportedOperationException();
			}

			@Override
			public String getPropertyValue(String arg0) {
				throw new UnsupportedOperationException();
			}

			@Override
			public String getVersion() {
				return null;
			}
		};
		@SuppressWarnings("rawtypes")
		final ThreadLocalContextManager threadLocalContextManager = new ThreadLocalContextManager() {
			@Override
			public void clearThreadLocalContext() {}

			@Override
			public Object getThreadLocalContext() {
				return null;
			}

			@Override
			public void setThreadLocalContext(Object context) {}
		};

		final DefaultHttpClientFactory defaultHttpClientFactory = new DefaultHttpClientFactory(eventPublisher, applicationProperties, threadLocalContextManager);
		final HttpClient httpClient = defaultHttpClientFactory.create(options);
		return new AtlassianHttpClientDecorator(httpClient, authenticationHandler) {
			@Override
			public void destroy() throws Exception {
				defaultHttpClientFactory.dispose(httpClient);
			}
		};
	}

	public static JIRAServer load() {
		final JIRAServerBuilder builder = JIRAServer.builder();
		builder.protocol(new PropertyStringInput("jira.protocol").fallback(NullableOptional.of("https")).get());
		builder.host(new PropertyStringInput("jira.host").fallback(new UserStringInput("Host", true)).get());
		builder.port(Integer.valueOf(new PropertyStringInput("jira.port").fallback(NullableOptional.of("0")).get()));
		builder.username(new PropertyStringInput("jira.username").fallback(new UserStringInput("Username", true)).get());
		builder.password(new PropertyStringInput("jira.password").fallback(new UserPasswordInput(String.format("Password for %1$s", builder.username))).get());
		return builder.build();
	}

	protected final String protocol;

	protected final String host;

	protected final int port;

	protected final String username;

	protected final String password;

	public JiraRestClient connect(boolean acceptSelfSignedCertificates) throws URISyntaxException {
		final String protocol = getProtocol();
		final int port = getPort();
		final URI uri = new URI(String.format("%1$s://%2$s", (protocol == null) ? "https" : protocol, getHost()) + ((port == 0) ? "" : ":" + port));

		final HttpClientOptions options = new HttpClientOptions();
		options.setTrustSelfSignedCertificates(acceptSelfSignedCertificates);
		final String username = getUsername();
		return new AsynchronousJiraRestClientFactory().create(uri, createClient(uri, (username == null) ? null : new BasicHttpAuthenticationHandler(username, getPassword()), options));
	}
}
