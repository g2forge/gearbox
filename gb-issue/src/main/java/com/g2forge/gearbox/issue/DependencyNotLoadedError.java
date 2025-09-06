package com.g2forge.gearbox.issue;

public class DependencyNotLoadedError extends LinkageError {
	private static final long serialVersionUID = 2218166938261285007L;

	public DependencyNotLoadedError() {}

	public DependencyNotLoadedError(String message) {
		super(message);
	}

	public DependencyNotLoadedError(String message, Throwable cause) {
		super(message, cause);
	}

	public DependencyNotLoadedError(String module, NoClassDefFoundError error) {
		this("Optional (provided scope) maven module " + module + " was not loaded, please add a compile dependency to your project!", (Throwable) error);
	}
}
