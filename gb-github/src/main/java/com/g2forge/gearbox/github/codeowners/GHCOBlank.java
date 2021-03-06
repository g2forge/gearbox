package com.g2forge.gearbox.github.codeowners;

import com.g2forge.alexandria.java.core.marker.ISingleton;

public class GHCOBlank implements IGHCOLine, ISingleton {
	protected static final GHCOBlank INSTANCE = new GHCOBlank();
	
	public static GHCOBlank create() {
		return INSTANCE;
	}
	
	protected GHCOBlank() {}
}
