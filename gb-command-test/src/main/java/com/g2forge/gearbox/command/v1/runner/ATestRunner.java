package com.g2forge.gearbox.command.v1.runner;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.v1.IUtils;
import com.g2forge.gearbox.command.v1.proxy.Proxifier;

import lombok.Getter;

public abstract class ATestRunner {
	protected final Proxifier proxifier = new Proxifier();

	@Getter(lazy = true)
	private final IFunction1<CommandInvocation<IRedirect, IRedirect>, IProcess> runner = createRunner();

	@Getter(lazy = true)
	private final IUtils utils = proxifier.generate(getRunner(), IUtils.class);

	protected abstract IFunction1<CommandInvocation<IRedirect, IRedirect>, IProcess> createRunner();

	@Test
	public void exitcode() {
		Assert.assertFalse(getUtils().false_());
		Assert.assertTrue(getUtils().true_());
	}
}
