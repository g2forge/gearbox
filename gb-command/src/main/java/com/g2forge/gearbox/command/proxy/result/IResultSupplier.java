package com.g2forge.gearbox.command.proxy.result;

import com.g2forge.alexandria.command.stdio.StandardIO;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.process.redirect.InheritRedirect;

@FunctionalInterface
public interface IResultSupplier<T> extends IFunction1<IProcess, T> {
	public static final StandardIO<IRedirect, IRedirect> STDIO_INHERIT = StandardIO.of(InheritRedirect.create());

	public static final StandardIO<IRedirect, IRedirect> STDIO_DEFAULT = null;

	public default StandardIO<IRedirect, IRedirect> createRedirect() {
		return STDIO_DEFAULT;
	}
}
