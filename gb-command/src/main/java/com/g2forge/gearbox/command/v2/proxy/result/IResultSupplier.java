package com.g2forge.gearbox.command.v2.proxy.result;

import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.command.process.IProcess;

@FunctionalInterface
public interface IResultSupplier<T> extends IFunction1<IProcess, T> {}
