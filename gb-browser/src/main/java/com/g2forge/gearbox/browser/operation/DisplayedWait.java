package com.g2forge.gearbox.browser.operation;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

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
	public class Wrapper extends LoadWait.Wrapper {
		public Wrapper(IBrowser browser) {
			super(browser);
		}

		@Override
		public void post() {
			super.post();
			if (isDisplayed()) browser.operation().until(b -> b.find(by)).operation().until(IElement::isDisplayed);
			else browser.operation().until(b -> {
				try {
					return !b.find(by).isDisplayed();
				} catch (NoSuchElementException e) {
					return true;
				}
			});
		}
	}

	protected final By by;

	protected final boolean displayed;

	@Override
	public IOperationWrapper apply(IBrowsable browsable) {
		return new Wrapper(browsable.getBrowser());
	}
}
