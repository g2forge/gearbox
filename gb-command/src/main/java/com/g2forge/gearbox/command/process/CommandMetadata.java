package com.g2forge.gearbox.command.process;

import java.util.List;

import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class CommandMetadata {
	protected final ISubject command;

	@Singular
	protected final List<ISubject> arguments;
}
