package com.g2forge.gearbox.browser;

import com.g2forge.gearbox.browser.operation.IOperationBuilder;

public interface IElement extends IBrowsable {
	public IElement clear();

	public IElement click();

	public String getAttribute(String attribute);

	public String getText();

	public boolean isDisplayed();

	public IElement moveTo();

	public IElement send(String text);

	@Override
	public IOperationBuilder<? extends IElement> operation();
}
