package com.g2forge.gearbox.command.v2.test;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.ProcessBuilderRunner;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.v2.IUtils;
import com.g2forge.gearbox.command.v2.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.v2.proxy.CommandProxyFactory;
import com.g2forge.gearbox.command.v2.proxy.ICommandProxyFactory;

import lombok.Getter;

public abstract class ATestCommand {
	@Getter(lazy = true)
	private final ICommandProxyFactory factory = new CommandProxyFactory(createRenderer(), createRunner());

	@Getter(lazy = true)
	private final IUtils utils = getFactory().apply(IUtils.class);

	protected ICommandConverterR_ createRenderer() {
		return null;
	}

	protected IFunction1<CommandInvocation<IRedirect, IRedirect>, IProcess> createRunner() {
		return new ProcessBuilderRunner(null);
	}

	@Test
	public void exitcode() {
		Assert.assertFalse(getUtils().false_());
		Assert.assertTrue(getUtils().true_());
	}
}
