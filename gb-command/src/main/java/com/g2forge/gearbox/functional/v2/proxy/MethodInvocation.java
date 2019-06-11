package com.g2forge.gearbox.functional.v2.proxy;

import java.lang.reflect.Method;
import java.util.Collection;

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
	protected final Collection<Object> arguments;
}
