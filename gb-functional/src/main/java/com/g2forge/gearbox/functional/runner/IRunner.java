package com.g2forge.gearbox.functional.runner;

import java.util.List;

import com.g2forge.alexandria.java.core.helpers.HCollection;

public interface IRunner {
	public IProcess run(List<String> arguments);

	public default IProcess run(String... arguments) {
		return run(HCollection.asList(arguments));
	}
}
