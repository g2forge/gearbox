package com.g2forge.gearbox.github.codeowners;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class GHCodeOwners {
	@Singular
	protected final List<IGHCOLine> lines;
	public static final String GITHUB_CODEOWNERS = ".github/CODEOWNERS";
}
