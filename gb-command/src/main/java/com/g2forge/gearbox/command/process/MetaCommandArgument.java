package com.g2forge.gearbox.command.process;

import java.util.Collection;
import java.util.List;

import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class MetaCommandArgument {
	public static List<String> toStrings(Collection<? extends MetaCommandArgument> arguments) {
		return arguments.stream().map(MetaCommandArgument::getValue).toList();
	}

	protected final String value;

	protected final ISubject meta;
}
