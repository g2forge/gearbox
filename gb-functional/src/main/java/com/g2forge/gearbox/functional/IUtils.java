package com.g2forge.gearbox.functional;

import java.nio.file.Path;

public interface IUtils {
	public String echo(@Flag("-e") boolean e, String... args);

	public String pwd(@Working Path working, @Flag("-P") boolean physical);
}
