package com.g2forge.gearbox.functional.control;

import com.g2forge.alexandria.java.function.IFunction2;
import com.g2forge.gearbox.functional.runner.IProcess;
import com.g2forge.gearbox.functional.runner.redirect.Redirects;

public interface IExplicitResultHandler extends IFunction2<IProcess, IResultContext, Object> {
	public Object apply(IProcess process, IResultContext context);

	public default Redirects getRedirects() {
		return null;
	}
}
