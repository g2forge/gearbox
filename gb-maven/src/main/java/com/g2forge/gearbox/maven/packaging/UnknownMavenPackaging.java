package com.g2forge.gearbox.maven.packaging;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class UnknownMavenPackaging implements IMavenPackaging {
	protected final String name;
}
