package com.g2forge.gearbox.functional.runner;

import java.io.InputStream;

import com.g2forge.alexandria.java.close.ICloseable;

public interface IProcess extends ICloseable {
	public InputStream getStandardOut();

	public int getExitCode();
}
