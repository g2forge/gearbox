package com.g2forge.gearbox.command.v1.control;

import com.g2forge.alexandria.command.stdio.IStandardIO;
import com.g2forge.alexandria.java.function.IFunction2;
import com.g2forge.gearbox.command.v1.runner.IProcess;
import com.g2forge.gearbox.command.v1.runner.redirect.IRedirect;

public interface IExplicitResultHandler extends IFunction2<IProcess, IResultContext, Object> {
	public Object apply(IProcess process, IResultContext context);

	public default IStandardIO<IRedirect, IRedirect> getRedirects() {
		return null;
	}
}
