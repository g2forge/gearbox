package com.g2forge.gearbox.issue;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.g2forge.alexandria.java.io.TextRangeSpecifier;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class ExamplePayload {
	protected final Path path;

	@JsonUnwrapped
	protected final TextRangeSpecifier range;

	@Override
	public String toString() {
		return getPath() + ":" + getRange().toRangeString();
	}
}