package com.g2forge.gearbox.functional.runner;

import com.g2forge.alexandria.command.Invocation;
import com.g2forge.gearbox.functional.runner.redirect.IRedirect;

public interface IRunner {
	public IProcess run(Invocation<IRedirect, IRedirect> invocation);
}
