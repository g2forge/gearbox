package com.g2forge.gearbox.browser.operation;

import org.openqa.selenium.By;

import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.browser.IBrowsable;
import com.g2forge.gearbox.browser.IBrowser;
import com.g2forge.gearbox.browser.IElement;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class DisplayedWait implements IFunction1<IBrowsable, IOperationWrapper> {
	public static class Wrapper extends LoadWait.Wrapper {
		protected final By by;

		public Wrapper(IBrowser browser, By by) {
			super(browser);
			this.by = by;
		}

		@Override
		public void post() {
			super.post();
			browser.operation().until(b -> b.find(by)).operation().until(IElement::isDisplayed);
		}

		@Override
		public void pre() {
			super.pre();
		}
	}

	protected final By by;

	@Override
	public IOperationWrapper apply(IBrowsable browsable) {
		return new Wrapper(browsable.getBrowser(), getBy());
	}
}
