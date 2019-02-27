package com.g2forge.gearbox.browser.operation;

import org.openqa.selenium.By;

import com.g2forge.alexandria.java.core.iface.ISingleton;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.browser.IBrowsable;
import com.g2forge.gearbox.browser.IBrowser;
import com.g2forge.gearbox.browser.IElement;

public class ChangeWait implements IFunction1<IBrowsable, IOperationWrapper>, ISingleton {
	protected static class Wrapper extends LoadWait.Wrapper {
		protected IElement original = null;

		public Wrapper(IBrowser browser) {
			super(browser);
		}

		public IElement getHTML(IBrowser browser) {
			return browser.find(By.tagName("html"));
		}

		@Override
		public void post() {
			browser.operation().until(b -> !getHTML(b).equals(original));
			super.post();
		}

		@Override
		public void pre() {
			super.pre();
			original = getHTML(browser);
		}
	}

	protected static final ChangeWait INSTANCE = new ChangeWait();

	public static ChangeWait create() {
		return INSTANCE;
	}

	@Override
	public IOperationWrapper apply(IBrowsable browsable) {
		return new Wrapper(browsable.getBrowser());
	}
}
