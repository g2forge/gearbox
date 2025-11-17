package com.g2forge.gearbox.command.proxy.result;

import com.g2forge.alexandria.command.stdio.StandardIO;
import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.redirect.IRedirect;

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

	@Override
	public StandardIO<IRedirect, IRedirect> createRedirect() {
		return STDIO_INHERIT;
	}
}