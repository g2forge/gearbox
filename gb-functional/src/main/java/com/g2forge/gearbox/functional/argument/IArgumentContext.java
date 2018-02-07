package com.g2forge.gearbox.functional.argument;

import com.g2forge.gearbox.functional.runner.Command;

public interface IArgumentContext {
	public IArgument<Object> getArgument();

	public Command.CommandBuilder getCommand();
}
