package com.g2forge.gearbox.command.which;

import org.junit.Test;

import com.g2forge.alexandria.java.core.enums.EnumException;
import com.g2forge.alexandria.java.platform.HPlatform;
import com.g2forge.alexandria.java.platform.PlatformCategory;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.process.ProcessBuilderRunner;
import com.g2forge.gearbox.command.proxy.CommandProxyFactory;

public class TestIWhichLike {
	@Test
	public void which() {
		final String command;
		final PlatformCategory category = HPlatform.getPlatform().getCategory();
		switch (category) {
			case Microsoft:
				command = "cmd.exe";
				break;
			case Posix:
				command = "bash";
				break;
			default:
				throw new EnumException(PlatformCategory.class, category);
		}

		final IWhichLike which = new CommandProxyFactory(DumbCommandConverter.create(), new ProcessBuilderRunner()).apply(IWhichLike.class);
		HAssert.assertTrue(which.isInstalled(command));
	}
}
