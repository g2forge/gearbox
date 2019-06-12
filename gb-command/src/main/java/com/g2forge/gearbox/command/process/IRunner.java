package com.g2forge.gearbox.command.process;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.command.process.redirect.IRedirect;

/**
 * Marker interface for all process runners. Note that while you might implement this interface, you should never pass instances of it around. Please use the
 * {@link IFunction1} for that.
 */
@FunctionalInterface
public interface IRunner extends IFunction1<CommandInvocation<IRedirect, IRedirect>, IProcess> {}
