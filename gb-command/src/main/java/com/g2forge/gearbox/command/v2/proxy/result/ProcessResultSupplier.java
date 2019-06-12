package com.g2forge.gearbox.command.v2.proxy.result;

import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.gearbox.command.process.IProcess;

public class ProcessResultSupplier implements IResultSupplier<IProcess>, ISingleton {
	protected static final ProcessResultSupplier INSTANCE = new ProcessResultSupplier();

	public static IResultSupplier<IProcess> create() {
		return INSTANCE;
	}

	@Override
	public IProcess apply(IProcess process) {
		return process;
	}
}