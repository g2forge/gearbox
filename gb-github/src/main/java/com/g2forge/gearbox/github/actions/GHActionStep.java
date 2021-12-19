package com.g2forge.gearbox.github.actions;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class GHActionStep {
	protected final String name;

	protected final String uses;

	@JsonProperty("working-directory")
	protected final String workingDirectory;

	protected final String run;

	@Singular("with")
	protected final Map<String, String> with;

	@Singular("env")
	protected final Map<String, String> env;
}
