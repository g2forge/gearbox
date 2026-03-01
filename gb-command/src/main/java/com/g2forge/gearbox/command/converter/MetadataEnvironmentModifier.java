package com.g2forge.gearbox.command.converter;

import com.g2forge.alexandria.command.invocation.environment.modified.IEnvironmentModifier;
import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class MetadataEnvironmentModifier implements IEnvironmentModifier {
	protected final ISubject subject;

	protected final IEnvironmentModifier modifier;

	@Override
	public String modify(String value) {
		if (getModifier() == null) return value;
		return getModifier().modify(value);
	}
}
