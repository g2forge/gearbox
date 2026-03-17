package com.g2forge.gearbox.command.log;

import java.util.Map;

import com.g2forge.alexandria.java.core.marker.ISingleton;

public class NopLogArgumentRewriter implements ILogArgumentRewriter, ISingleton {
	protected static final NopLogArgumentRewriter INSTANCE = new NopLogArgumentRewriter();

	public static NopLogArgumentRewriter create() {
		return INSTANCE;
	}

	protected NopLogArgumentRewriter() {}

	@Override
	public String rewrite(String argument, Map<String, Object> context) {
		return argument;
	}
}
