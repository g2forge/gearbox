package com.g2forge.gearbox.jira.fields;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class Field implements IField {
	protected final String name;

	protected final String withKey;
}
