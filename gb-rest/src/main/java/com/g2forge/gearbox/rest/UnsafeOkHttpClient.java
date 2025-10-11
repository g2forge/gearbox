package com.g2forge.gearbox.rest;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import com.g2forge.alexandria.java.io.net.UnsafeX509TrustManager;

import okhttp3.OkHttpClient;

@SuppressWarnings("deprecation")
public class UnsafeOkHttpClient {
	public static OkHttpClient.Builder configure(final OkHttpClient.Builder clientBuilder) {
		final X509TrustManager trustManager = UnsafeX509TrustManager.create();
		clientBuilder.sslSocketFactory(createUnsafeSSLSocketFacory(), trustManager);
		clientBuilder.hostnameVerifier((hostname, session) -> true);
		return clientBuilder;
	}

	public static SSLSocketFactory createUnsafeSSLSocketFacory() {
		final SSLContext sslContext = UnsafeX509TrustManager.getUnsafeSSLContext();
		return sslContext.getSocketFactory();
	}
}