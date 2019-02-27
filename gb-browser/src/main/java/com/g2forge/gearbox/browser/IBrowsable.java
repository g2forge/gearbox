package com.g2forge.gearbox.browser;

import com.g2forge.gearbox.browser.operation.IOperationBuilder;

public interface IBrowsable {
	public IBrowser getBrowser();

	public IOperationBuilder<? extends IBrowsable> operation();
}
