package com.g2forge.gearbox.command.v2.proxy.result;

import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.gearbox.command.v2.process.IProcess;

public class IntegerResultSupplier implements IResultSupplier<Integer>, ISingleton {
	protected static final IntegerResultSupplier INSTANCE = new IntegerResultSupplier();

	public static IResultSupplier<Integer> create() {
		return INSTANCE;
	}

	@Override
	public Integer apply(IProcess process) {
		try {
			return process.getExitCode();
		} finally {
			process.close();
		}
	}
}