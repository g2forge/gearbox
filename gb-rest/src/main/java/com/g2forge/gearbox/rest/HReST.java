package com.g2forge.gearbox.rest;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.g2forge.alexandria.java.core.marker.Helpers;
import com.g2forge.alexandria.java.function.ISupplier;
import com.g2forge.alexandria.java.io.RuntimeIOException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Helpers
@UtilityClass
public class HReST {
	@Getter(value = AccessLevel.PUBLIC, lazy = true)
	private static final ObjectMapper mapper = new ObjectMapper();

	public static <T> T create(Class<T> type, String url, Map<String, String> headers) {
		final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
		UnsafeOkHttpClient.configure(okHttpClientBuilder);
		//okHttpClientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY));
		if ((headers != null) && !headers.isEmpty()) okHttpClientBuilder.addInterceptor(new Interceptor() {
			@Override
			public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
				final Request original = chain.request();
				final Request.Builder builder = original.newBuilder().method(original.method(), original.body());
				for (Map.Entry<String, String> header : headers.entrySet()) {
					builder.addHeader(header.getKey(), header.getValue());
				}
				return chain.proceed(builder.build());
			}
		});
		final OkHttpClient okHttpClient = okHttpClientBuilder.build();

		final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
		retrofitBuilder.client(okHttpClient);
		retrofitBuilder.addConverterFactory(JacksonConverterFactory.create(getMapper()));
		retrofitBuilder.baseUrl(url);
		final Retrofit retrofit = retrofitBuilder.build();
		return retrofit.create(type);
	}

	public static <API, ReturnType> ReturnType invoke(ISupplier<? extends Call<? extends ReturnType>> supplier) {
		final retrofit2.Response<? extends ReturnType> response;
		try {
			final Call<? extends ReturnType> call = supplier.get();
			response = call.execute();
			if (!response.isSuccessful()) try (final ResponseBody errorBody = response.errorBody()) {
				throw new RuntimeException(errorBody.string());
			}
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
		return response.body();
	}
}
