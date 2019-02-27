package com.g2forge.gearbox.browser.operation;

import com.g2forge.alexandria.java.function.IRunnable;

public interface IOperationWrapper {
	public void post();

	public void pre();

	public default IOperationWrapper wrap(IOperationWrapper inner) {
		return new IOperationWrapper() {
			@Override
			public void post() {
				inner.post();
				IOperationWrapper.this.post();
			}

			@Override
			public void pre() {
				IOperationWrapper.this.pre();
				inner.pre();
			}
		};
	}

	public default IRunnable wrap(IRunnable runnable) {
		return runnable.wrap(this::pre, this::post);
	}
}