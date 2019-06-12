package com.g2forge.gearbox.command.v2.proxy.result;

import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.gearbox.command.process.IProcess;

public class BooleanResultSupplier implements IResultSupplier<Boolean>, ISingleton {
	protected static final BooleanResultSupplier INSTANCE = new BooleanResultSupplier();

	public static IResultSupplier<Boolean> create() {
		return INSTANCE;
	}

	@Override
	public Boolean apply(IProcess process) {
		try {
			return process.getExitCode() == 0;
		} finally {
			process.close();
		}
	}
}