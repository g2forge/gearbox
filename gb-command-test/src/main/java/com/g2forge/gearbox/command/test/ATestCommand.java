package com.g2forge.gearbox.command.test;

import org.junit.Test;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.alexandria.test.HAssume;
import com.g2forge.gearbox.command.IUtils;
import com.g2forge.gearbox.command.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.ProcessBuilderRunner;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.proxy.CommandProxyFactory;
import com.g2forge.gearbox.command.proxy.ICommandProxyFactory;

import lombok.Getter;

public abstract class ATestCommand {
	@Getter(lazy = true)
	private final ICommandProxyFactory factory = new CommandProxyFactory(createRenderer(), getRunner());

	@Getter(lazy = true)
	private final IFunction1<CommandInvocation<IRedirect, IRedirect>, IProcess> runner = createRunner();

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
		HAssume.assumeTrue(isValid());
		HAssert.assertFalse(getUtils().false_());
		HAssert.assertTrue(getUtils().true_());
	}

	protected boolean isValid() {
		return true;
	}
}
