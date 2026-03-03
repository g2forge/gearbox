package com.g2forge.gearbox.command.nested;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.command.invocation.CommandInvocation.CommandInvocationBuilder;
import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.gearbox.command.process.redirect.IRedirect;

@FunctionalInterface
public interface IArgumentModifier extends IConsumer2<CommandInvocation<IRedirect, IRedirect>, CommandInvocationBuilder<IRedirect, IRedirect>> {}
