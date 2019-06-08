package com.g2forge.gearbox.functional.runner.redirect;

import com.g2forge.alexandria.java.core.marker.ISingleton;

public class InheritRedirect implements IRedirect, ISingleton {
	protected static final InheritRedirect instance = new InheritRedirect();

	public static InheritRedirect create() {
		return instance;
	}
}
