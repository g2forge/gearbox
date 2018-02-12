package com.g2forge.gearbox.functional.proxy;

import java.nio.file.Path;

import com.g2forge.gearbox.functional.control.ICommandBuilder;
import com.g2forge.gearbox.functional.runner.Command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class CommandBuilder implements ICommandBuilder {
	protected final Command.CommandBuilder commandBuilder;

	@Override
	public ICommandBuilder argument(String argument) {
		getCommandBuilder().argument(argument);
		return this;
	}

	@Override
	public ICommandBuilder working(Path working) {
		getCommandBuilder().working(working);
		return this;
	}
}