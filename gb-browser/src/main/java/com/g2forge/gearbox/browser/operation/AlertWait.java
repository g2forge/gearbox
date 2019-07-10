package com.g2forge.gearbox.browser.operation;

import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.browser.IAlert;
import com.g2forge.gearbox.browser.IBrowsable;
import com.g2forge.gearbox.browser.IBrowser;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AlertWait implements IFunction1<IBrowsable, IOperationWrapper> {
	@RequiredArgsConstructor
	public static class Wrapper implements IOperationWrapper {
		protected final IBrowser browser;

		protected final IConsumer1<? super IAlert> consumer;

		@Override
		public void post() {
			browser.operation().until(b -> {
				final IAlert alert = b.getAlert();
				if (alert == null) return false;
				consumer.accept(alert);
				return true;
			});
			browser.operation().until(b -> b.getAlert() == null);
		}

		@Override
		public void pre() {}
	}

	protected final IConsumer1<? super IAlert> consumer;

	@Override
	public IOperationWrapper apply(IBrowsable browsable) {
		return new Wrapper(browsable.getBrowser(), getConsumer());
	}
}
