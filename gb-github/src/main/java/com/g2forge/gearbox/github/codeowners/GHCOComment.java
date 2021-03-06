package com.g2forge.gearbox.github.codeowners;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class GHCOComment implements IGHCOLine {
	protected final String comment;
}
