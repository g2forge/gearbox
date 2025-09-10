package com.g2forge.gearbox.issue.document;

import java.util.List;

import com.g2forge.alexandria.java.core.helpers.HCollection;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class DocumentPayload {
	@Singular
	protected final List<String> items;

	public DocumentPayload(String... items) {
		this(HCollection.asList(items));
	}
}
