package com.g2forge.gearbox.command.proxy.result;

import com.g2forge.alexandria.command.stdio.StandardIO;
import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.habitat.metadata.value.subject.ISubject;

public class VoidResultSupplier implements IResultSupplier<Void>, ISingleton {
	protected static final VoidResultSupplier INSTANCE = new VoidResultSupplier();

	public static IResultSupplier<Void> create() {
		return INSTANCE;
	}

	@Override
	public Void apply(ISubject subject, IProcess process) {
		try {
			process.assertSuccess();
			return null;
		} finally {
			process.close();
		}
	}

	@Override
	public StandardIO<IRedirect, IRedirect> createRedirect() {
		return STDIO_INHERIT;
	}
}