package com.g2forge.gearbox.maven;

import com.g2forge.alexandria.command.invocation.runner.IdentityCommandRunner;
import com.g2forge.alexandria.java.core.marker.Helpers;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.process.ProcessBuilderRunner;
import com.g2forge.gearbox.command.proxy.CommandProxyFactory;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
@Helpers
public class HMaven {
	@Getter(lazy = true)
	private final IMaven maven = new CommandProxyFactory(DumbCommandConverter.create(), new ProcessBuilderRunner(IdentityCommandRunner.create())).apply(IMaven.class);
}
