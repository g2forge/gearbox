package com.g2forge.gearbox.command.v1.control;

import com.g2forge.alexandria.java.function.IFunction1;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ModifyCommandException extends RuntimeException {
	private static final long serialVersionUID = 7132148984657635292L;

	@Getter
	protected final IFunction1<? super TypedInvocation, ? extends TypedInvocation> modifier;
}
