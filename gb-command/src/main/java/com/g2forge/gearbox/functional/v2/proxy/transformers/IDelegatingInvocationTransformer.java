package com.g2forge.gearbox.functional.v2.proxy.transformers;

import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.functional.v2.process.ProcessInvocation;
import com.g2forge.gearbox.functional.v2.proxy.MethodInvocation;

public interface IDelegatingInvocationTransformer extends IInvocationTransformer {
	public IFunction1<MethodInvocation, ProcessInvocation<?>> getDelegate();
}
