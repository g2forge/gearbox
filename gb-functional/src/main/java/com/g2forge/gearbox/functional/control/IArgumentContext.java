package com.g2forge.gearbox.functional.control;

import com.g2forge.gearbox.functional.runner.Command;

public interface IArgumentContext {
	public IArgument<Object> getArgument();

	public Command.CommandBuilder getCommand();
}
