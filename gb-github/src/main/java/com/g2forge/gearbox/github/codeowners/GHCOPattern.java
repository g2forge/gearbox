package com.g2forge.gearbox.github.codeowners;

import java.util.List;

import com.g2forge.alexandria.java.core.helpers.HCollection;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class GHCOPattern implements IGHCOLine {
	protected final String pattern;

	@Singular
	protected final List<String> owners;
	
	public GHCOPattern(String pattern, String...owners) {
		this(pattern, HCollection.asList(owners));
	}
}
