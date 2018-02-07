package com.g2forge.gearbox.functional;

import java.nio.file.Path;

import com.g2forge.gearbox.functional.argument.Flag;
import com.g2forge.gearbox.functional.argument.Working;

public interface IUtils {
	public String echo(@Flag("-e") boolean e, String... args);

	public String pwd(@Working Path working, @Flag("-P") boolean physical);
}
