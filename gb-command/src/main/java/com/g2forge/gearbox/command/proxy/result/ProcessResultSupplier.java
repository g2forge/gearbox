package com.g2forge.gearbox.command.proxy.result;

import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.habitat.metadata.value.subject.ISubject;

public class ProcessResultSupplier implements IResultSupplier<IProcess>, ISingleton {
	protected static final ProcessResultSupplier INSTANCE = new ProcessResultSupplier();

	public static IResultSupplier<IProcess> create() {
		return INSTANCE;
	}

	@Override
	public IProcess apply(ISubject subject, IProcess process) {
		return process;
	}
}