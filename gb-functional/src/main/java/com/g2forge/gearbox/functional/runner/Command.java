package com.g2forge.gearbox.functional.runner;

import java.nio.file.Path;
import java.util.List;

import com.g2forge.alexandria.java.core.helpers.HCollection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
@AllArgsConstructor
public class Command {
	protected final Path working;

	@Singular
	protected final List<String> arguments;

	public Command(Path working, String... arguments) {
		this(working, HCollection.asList(arguments));
	}
}