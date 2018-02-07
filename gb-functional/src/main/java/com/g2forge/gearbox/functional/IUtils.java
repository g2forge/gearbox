package com.g2forge.gearbox.functional;

import java.nio.file.Path;

import com.g2forge.gearbox.functional.control.Command;
import com.g2forge.gearbox.functional.control.Flag;
import com.g2forge.gearbox.functional.control.Working;

public interface IUtils {
	public String echo(@Flag("-e") boolean e, String... args);

	@Command("false")
	public boolean false_();

	public String pwd(@Working Path working, @Flag("-P") boolean physical);

	@Command("true")
	public boolean true_();
}
