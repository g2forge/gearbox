package com.g2forge.gearbox.functional.control;

import java.nio.file.Path;

public interface ICommandBuilder {
	public ICommandBuilder argument(String argument);

	public ICommandBuilder working(Path working);
}
