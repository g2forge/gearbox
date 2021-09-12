package com.g2forge.gearbox.rest;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class UnsafeOkHttpClient {
	public static final X509TrustManager unsafeX509TrustManager = new X509TrustManager() {
		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}
	};

	public static OkHttpClient.Builder configure(final OkHttpClient.Builder clientBuilder) {
		clientBuilder.sslSocketFactory(createUnsafeSSLSocketFacory(), unsafeX509TrustManager);
		clientBuilder.hostnameVerifier((hostname, session) -> true);
		return clientBuilder;
	}

	public static SSLSocketFactory createUnsafeSSLSocketFacory() {
		try {
			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { unsafeX509TrustManager }, new java.security.SecureRandom());
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			return sslSocketFactory;
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}
}