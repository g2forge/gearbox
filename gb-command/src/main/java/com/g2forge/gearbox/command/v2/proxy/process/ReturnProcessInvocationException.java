package com.g2forge.gearbox.command.v2.proxy.process;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReturnProcessInvocationException extends RuntimeException {
	private static final long serialVersionUID = -4657201513721664083L;

	protected final ProcessInvocation<?> processInvocation;
}
