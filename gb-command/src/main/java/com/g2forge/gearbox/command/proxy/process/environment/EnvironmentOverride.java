package com.g2forge.gearbox.command.proxy.process.environment;

/**
 * Specify how to generate the value for an environment variable.
 * 
 * @see EnvironmentValue
 */
public enum EnvironmentOverride implements IEnvironmentValue {
	/** Inherit the environment variable from the parent process. */
	Inherit {
		@Override
		public String modify(String value) {
			return value;
		}
	},
	/** Ensure the variable is not specified to the child process. */
	Unspecified {
		@Override
		public String modify(String value) {
			return null;
		}
	};
}