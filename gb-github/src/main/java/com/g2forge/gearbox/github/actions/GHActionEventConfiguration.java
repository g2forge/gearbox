package com.g2forge.gearbox.github.actions;

import java.util.List;

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
public class GHActionEventConfiguration {
	@Singular
	protected final List<String> branches;
	
	@Singular
	protected final List<String> tags;
	
	@Singular
	protected final List<GHActionEventType> types;
}
