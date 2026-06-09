package com.g2forge.gearbox.command.proxy.result;

import com.g2forge.alexandria.command.stdio.StandardIO;
import com.g2forge.alexandria.java.function.IFunction2;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.process.redirect.InheritRedirect;
import com.g2forge.habitat.metadata.value.subject.ISubject;

@FunctionalInterface
public interface IResultSupplier<T> extends IFunction2<ISubject, IProcess, T> {
	public static final StandardIO<IRedirect, IRedirect> STDIO_INHERIT = StandardIO.of(InheritRedirect.create());

	public static final StandardIO<IRedirect, IRedirect> STDIO_DEFAULT = null;

	public default StandardIO<IRedirect, IRedirect> createRedirect() {
		return STDIO_DEFAULT;
	}
}
