package com.g2forge.gearbox.functional.control;

public interface IArgumentContext {
	public IArgument<Object> getArgument();

	public ICommandBuilder getCommand();
}
