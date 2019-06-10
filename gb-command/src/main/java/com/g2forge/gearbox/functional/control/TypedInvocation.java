package com.g2forge.gearbox.functional.control;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.gearbox.functional.runner.redirect.IRedirect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class TypedInvocation {
	protected final CommandInvocation<IRedirect, IRedirect> invocation;

	protected final IExplicitResultHandler resultHandler;
}