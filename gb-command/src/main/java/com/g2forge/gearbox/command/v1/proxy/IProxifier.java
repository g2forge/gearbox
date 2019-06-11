package com.g2forge.gearbox.command.v1.proxy;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.command.v1.runner.IProcess;
import com.g2forge.gearbox.command.v1.runner.redirect.IRedirect;

public interface IProxifier {
	public <T> T generate(IFunction1<CommandInvocation<IRedirect, IRedirect>, IProcess> runner, Class<T> type);
}
