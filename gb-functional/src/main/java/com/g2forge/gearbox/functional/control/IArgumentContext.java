package com.g2forge.gearbox.functional.control;

import com.g2forge.alexandria.command.Invocation;
import com.g2forge.gearbox.functional.runner.redirect.IRedirect;

public interface IArgumentContext {
	public IArgument<Object> getArgument();

	public Invocation.InvocationBuilder<IRedirect, IRedirect> getCommand();
}
