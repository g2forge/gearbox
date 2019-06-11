package com.g2forge.gearbox.command.v1.control;

import com.g2forge.alexandria.java.function.IConsumer2;

public interface IExplicitArgumentHandler extends IConsumer2<IArgumentContext, Object> {
	public void accept(IArgumentContext context, Object argument);
}
