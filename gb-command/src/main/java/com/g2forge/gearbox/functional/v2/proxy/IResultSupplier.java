package com.g2forge.gearbox.functional.v2.proxy;

import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.functional.v2.process.IProcess;

public interface IResultSupplier<T> extends IFunction1<IProcess, T> {}
