package com.g2forge.gearbox.command.log;

import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class PasswordLogArgumentRewriter implements ILogArgumentRewriter {
	@Builder.Default
	protected final String rewrite = PasswordLog.DEFAULT_REPLACEMENT;

	public PasswordLogArgumentRewriter() {
		this(PasswordLog.DEFAULT_REPLACEMENT);
	}

	@Override
	public String rewrite(String argument, Map<String, Object> context) {
		return getRewrite();
	}
}
