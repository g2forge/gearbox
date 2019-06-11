package com.g2forge.gearbox.command.v2.proxy.method;

import java.lang.reflect.Method;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class MethodInvocation {
	protected final Object object;

	protected final Method method;

	@Singular
	protected final List<Object> arguments;
}
