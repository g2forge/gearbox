package com.g2forge.gearbox.rest;

import java.io.IOException;

import com.g2forge.alexandria.java.core.marker.Helpers;
import com.g2forge.alexandria.java.function.ISupplier;
import com.g2forge.alexandria.java.io.RuntimeIOException;

import lombok.experimental.UtilityClass;
import retrofit2.Call;
import retrofit2.Response;

@Helpers
@UtilityClass
public class HReST {
	public static <API, ReturnType> ReturnType invoke(ISupplier<? extends Call<? extends ReturnType>> supplier) {
		final Response<? extends ReturnType> response;
		try {
			final Call<? extends ReturnType> call = supplier.get();
			response = call.execute();
			if (!response.isSuccessful()) throw new RuntimeException(response.errorBody().string());
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
		return response.body();
	}
}
