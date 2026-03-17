package com.g2forge.gearbox.command.log;

import java.util.Map;

import com.g2forge.alexandria.java.core.marker.ISingleton;

public class SkipLogArgumentRewriter implements ILogArgumentRewriter, ISingleton {
	protected static final SkipLogArgumentRewriter INSTANCE = new SkipLogArgumentRewriter();

	public static SkipLogArgumentRewriter create() {
		return INSTANCE;
	}

	protected SkipLogArgumentRewriter() {}

	@Override
	public String rewrite(String argument, Map<String, Object> context) {
		return null;
	}
}
