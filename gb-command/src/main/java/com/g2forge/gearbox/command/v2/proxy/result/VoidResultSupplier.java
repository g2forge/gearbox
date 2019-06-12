package com.g2forge.gearbox.command.v2.proxy.result;

import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.gearbox.command.v2.process.IProcess;

public class VoidResultSupplier implements IResultSupplier<Void>, ISingleton {
	protected static final VoidResultSupplier INSTANCE = new VoidResultSupplier();

	public static IResultSupplier<Void> create() {
		return INSTANCE;
	}

	@Override
	public Void apply(IProcess process) {
		try {
			process.assertSuccess();
			return null;
		} finally {
			process.close();
		}
	}
}