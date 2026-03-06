package com.g2forge.gearbox.command.process;

import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class MetaCommandArgument {
	protected final String value;

	protected final ISubject meta;
}
