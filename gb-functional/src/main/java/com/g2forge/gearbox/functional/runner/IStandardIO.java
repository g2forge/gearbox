package com.g2forge.gearbox.functional.runner;

public interface IStandardIO<I, O> {
	public I getStandardInput();

	public O getStandardOutput();

	public O getStandardError();
}
