package com.g2forge.gearbox.browser.operation;

import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.browser.IBrowsable;
import com.g2forge.gearbox.browser.IBrowser;

public class LoadWait implements IFunction1<IBrowsable, IOperationWrapper>, ISingleton {
	public static class Wrapper implements IOperationWrapper {
		protected final IBrowser browser;

		public Wrapper(IBrowser browser) {
			this.browser = browser;
		}

		@Override
		public void post() {
			browser.operation().until(b -> b.executeJavascript("return document.readyState").equals("complete"));
		}

		@Override
		public void pre() {}
	}

	protected static final LoadWait INSTANCE = new LoadWait();

	public static LoadWait create() {
		return INSTANCE;
	}

	@Override
	public IOperationWrapper apply(IBrowsable browsable) {
		return new Wrapper(browsable.getBrowser());
	}
}
