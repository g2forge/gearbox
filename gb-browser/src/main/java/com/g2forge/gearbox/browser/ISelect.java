package com.g2forge.gearbox.browser;

import java.util.List;

import com.g2forge.gearbox.browser.operation.IOperationBuilder;

public interface ISelect extends IElement {
	public interface IOption extends IElement {
		public void setSelected(boolean selected);

		public boolean isSelected();
	}

	/**
	 * @throws UnsupportedOperationException if {@link #isMultiple()} returns <code>false</code>.
	 */
	public void deselectAll();

	public IOption getFirstSelected();

	public IOption getOptionByIndex(int index);

	public List<IOption> getOptionsByText(String text);

	public List<IOption> getOptionsByValue(String value);

	/**
	 * Get the options for this select.
	 * 
	 * @param selected <code>false</code> will return all options.
	 * @return Either all or the selected options.
	 */
	public List<IOption> getOptions(boolean selected);

	public boolean isMultiple();

	@Override
	public IOperationBuilder<? extends ISelect> operation();
}
