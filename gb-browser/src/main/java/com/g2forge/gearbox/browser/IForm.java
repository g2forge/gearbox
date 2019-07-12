package com.g2forge.gearbox.browser;

import java.util.List;

import org.openqa.selenium.By;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.gearbox.browser.ISelect.IOption;
import com.g2forge.gearbox.browser.operation.IOperationBuilder;

public interface IForm extends IElement {
	public boolean getBoolean(By by);

	public default ISelect.IOption getOption(By by) {
		final List<IOption> options = getOptions(by);
		if (options.isEmpty()) return null;
		return HCollection.getOne(options);
	}

	public List<ISelect.IOption> getOptions(By by);

	public String getText(By by);

	@Override
	public IOperationBuilder<? extends IForm> operation();

	public IForm select(By by, ISelect.IOptionSelector... selectors);

	public IForm set(By by, boolean selected);

	public IForm set(By by, String text);

	public IForm submit();
}
