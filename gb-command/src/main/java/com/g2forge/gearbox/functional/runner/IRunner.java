package com.g2forge.gearbox.functional.runner;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.gearbox.functional.runner.redirect.IRedirect;

public interface IRunner {
	public IProcess run(CommandInvocation<IRedirect, IRedirect> invocation);
}
