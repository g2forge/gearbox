package com.g2forge.gearbox.command;

import com.g2forge.alexandria.command.invocation.runner.ICommandRunner;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.process.ProcessBuilderRunner;
import com.g2forge.gearbox.command.proxy.CommandProxyFactory;
import com.g2forge.gearbox.command.proxy.ICommandProxyFactory;
import com.g2forge.gearbox.command.which.IWhichLike;

import lombok.AccessLevel;
import lombok.Getter;

public class HStandardCommand {
	@Getter(lazy = true)
	private static final IWhichLike which = getCommandProxyFactoryShell().apply(IWhichLike.class);

	@Getter(lazy = true, value = AccessLevel.PUBLIC)
	private static final ICommandProxyFactory commandProxyFactoryNative = new CommandProxyFactory(DumbCommandConverter.create(), new ProcessBuilderRunner(ICommandRunner.create()));

	@Getter(lazy = true, value = AccessLevel.PUBLIC)
	private static final ICommandProxyFactory commandProxyFactoryShell = new CommandProxyFactory(DumbCommandConverter.create(), new ProcessBuilderRunner());
}
