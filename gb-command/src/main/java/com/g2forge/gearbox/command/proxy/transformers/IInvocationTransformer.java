package com.g2forge.gearbox.command.proxy.transformers;

import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.command.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;

public interface IInvocationTransformer extends IFunction1<MethodInvocation, ProcessInvocation<?>> {}
