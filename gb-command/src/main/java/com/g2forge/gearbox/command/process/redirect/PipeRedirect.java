package com.g2forge.gearbox.command.process.redirect;

import com.g2forge.alexandria.java.core.marker.ISingleton;

public class PipeRedirect implements IRedirect, ISingleton {
	protected static final PipeRedirect instance = new PipeRedirect();

	public static PipeRedirect create() {
		return instance;
	}
}
