package com.g2forge.gearbox.command.v2.proxy.result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.gearbox.command.process.IProcess;

public class StringResultSupplier implements IResultSupplier<String>, ISingleton {
	protected static final StringResultSupplier INSTANCE = new StringResultSupplier();

	public static IResultSupplier<String> create() {
		return INSTANCE;
	}

	@Override
	public String apply(IProcess process) {
		try {
			final StringBuilder retVal = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getStandardOutput()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					retVal.append(line).append("\n");
				}
			} catch (IOException exception) {
				throw new RuntimeIOException(exception);
			}

			process.assertSuccess();
			return retVal.toString();
		} finally {
			process.close();
		}
	}
}