package com.g2forge.gearbox.command.v2.proxy.process;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.v2.proxy.result.IResultSupplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ProcessInvocation<O> {
	/**
	 * The command invocation. Can be <code>null</code> to indicate that no command should be run, and that a <code>null</code> process should be passed to
	 * {@link #resultSupplier}.
	 */
	protected final CommandInvocation<IRedirect, IRedirect> commandInvocation;

	protected final IResultSupplier<? extends O> resultSupplier;
	
	public IResultSupplier<? extends O> getResultSupplier() {
		return this.resultSupplier;
	}
}
