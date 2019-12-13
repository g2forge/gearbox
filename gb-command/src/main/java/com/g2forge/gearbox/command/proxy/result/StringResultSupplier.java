package com.g2forge.gearbox.command.proxy.result;

import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.alexandria.java.io.HIO;
import com.g2forge.gearbox.command.process.IProcess;

public class StringResultSupplier implements IResultSupplier<String>, ISingleton {
	protected static final StringResultSupplier INSTANCE = new StringResultSupplier();

	public static IResultSupplier<String> create() {
		return INSTANCE;
	}

	@Override
	public String apply(IProcess process) {
		try {
			process.assertSuccess();
			return HIO.readAll(process.getStandardOutput(), true);
		} finally {
			process.close();
		}
	}
}