package com.g2forge.gearbox.command.v2;

import java.nio.file.Path;

import com.g2forge.gearbox.command.v2.converter.dumb.Command;
import com.g2forge.gearbox.command.v2.converter.dumb.Flag;
import com.g2forge.gearbox.command.v2.converter.dumb.Working;

public interface IUtils {
	public String echo(@Flag("-e") boolean e, String... args);

	@Command("false")
	public boolean false_();

	public String pwd(@Working Path working, @Flag("-P") boolean physical);

	@Command("true")
	public boolean true_();
}
