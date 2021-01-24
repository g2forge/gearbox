package com.g2forge.gearbox.command.test;

import org.junit.Test;

import com.g2forge.gearbox.command.IUtils;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;

public class TestCommandTester {
	@Test
	public void echo() {
		new CommandTester<>(DumbCommandConverter.create(), IUtils.class).assertArguments(utils -> utils.echo(false, "Hello, World!"), "echo", "Hello, World!");
	}
}
