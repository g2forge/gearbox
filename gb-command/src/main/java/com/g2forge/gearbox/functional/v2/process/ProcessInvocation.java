package com.g2forge.gearbox.functional.v2.process;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.gearbox.functional.runner.redirect.IRedirect;
import com.g2forge.gearbox.functional.v2.proxy.IResultSupplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ProcessInvocation<O> {
	protected final CommandInvocation<IRedirect, IRedirect> invocation;

	protected final IResultSupplier<O> resultSupplier;
}
