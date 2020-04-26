package com.g2forge.gearbox.github.actions;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class GHActionWorkflow {
	protected final String name;

	@Singular("on")
	protected final Map<GHActionEvent, GHActionEventConfiguration> on;

	@Singular
	protected final Map<String, GHActionJob> jobs;
}
