package com.g2forge.gearbox.github;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class GHError {
	@Data
	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Error {
		protected String resource;
		protected String field;
		protected String code;
		protected String message;
	}

	protected String message;

	@Singular
	protected List<Error> errors;

	@JsonProperty("documentation_url")
	protected String documentationURL;
}
