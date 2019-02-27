package com.g2forge.gearbox.browser;

import org.openqa.selenium.By;

import com.g2forge.gearbox.browser.operation.IOperationBuilder;

public interface IForm extends IElement {
	public IForm set(By by, String test);
	
	public IForm submit();
	
	@Override
	public IOperationBuilder<? extends IForm> operation();
}
