package com.g2forge.gearbox.command.proxy.process.environment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Specify a string value for an environment variable.
 * 
 * @see EnvironmentOverride
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class EnvironmentValue implements IEnvironmentValue {
	protected final String value;

	@Override
	public String modify(String value) {
		return getValue();
	}
}