package com.g2forge.gearbox.functional.v2.process;

import com.g2forge.alexandria.java.function.IFunction1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ModifyProcessInvocationException extends RuntimeException {
	private static final long serialVersionUID = 8272821821857812621L;

	protected final IFunction1<? super ProcessInvocation<?>, ? extends ProcessInvocation<?>> function;
}
