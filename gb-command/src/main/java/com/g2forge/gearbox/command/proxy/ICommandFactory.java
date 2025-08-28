package com.g2forge.gearbox.command.proxy;

public interface ICommandFactory<T> {
	public T create(ICommandProxyFactory factory);
}
