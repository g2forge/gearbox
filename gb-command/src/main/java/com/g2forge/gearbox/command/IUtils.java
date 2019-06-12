package com.g2forge.gearbox.command;

import java.nio.file.Path;

import com.g2forge.gearbox.command.converter.dumb.Command;
import com.g2forge.gearbox.command.converter.dumb.Flag;
import com.g2forge.gearbox.command.converter.dumb.Working;
import com.g2forge.gearbox.command.proxy.method.ICommandInterface;

public interface IUtils extends ICommandInterface {
	public String echo(@Flag("-e") boolean e, String... args);

	@Command("false")
	public boolean false_();

	public String pwd(@Working Path working, @Flag("-P") boolean physical);

	@Command("true")
	public boolean true_();
}
