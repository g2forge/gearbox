package com.g2forge.gearbox.maven;

import com.g2forge.alexandria.java.core.marker.Helpers;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.process.ProcessBuilderRunner;
import com.g2forge.gearbox.command.proxy.CommandProxyFactory;
import com.g2forge.gearbox.command.proxy.ICommandProxyFactory;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
@Helpers
public class HMaven {
	@Getter(lazy = true)
	private static final IMaven maven = computeMaven();

	protected static IMaven computeMaven() {
		final ICommandProxyFactory factory = new CommandProxyFactory(DumbCommandConverter.create(), new ProcessBuilderRunner());
		return factory.apply(IMaven.class);
	}
}
