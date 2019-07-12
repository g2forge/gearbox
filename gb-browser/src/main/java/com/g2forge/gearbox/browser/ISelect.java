package com.g2forge.gearbox.browser;

import java.util.List;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.gearbox.browser.operation.IOperationBuilder;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

public interface ISelect extends IElement {
	public interface IOption extends IElement {
		public String getValue();

		public boolean isSelected();

		public void setSelected(boolean selected);
	}

	public interface IOptionSelector {}

	public static class OptionSelectAll implements IOptionSelector, ISingleton {
		protected static final OptionSelectAll instance = new OptionSelectAll();

		public static OptionSelectAll create() {
			return instance;
		}
	}

	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	public static class OptionSelectByIndex implements IOptionSelector {
		protected final int index;
	}

	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	public static class OptionSelectBySelected implements IOptionSelector {
		protected final boolean selected;
	}

	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	public static class OptionSelectByText implements IOptionSelector {
		protected final String text;
	}

	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	public static class OptionSelectByValue implements IOptionSelector {
		protected final String value;
	}

	public static IOptionSelector byAll() {
		return OptionSelectAll.create();
	}

	public static IOptionSelector byIndex(int index) {
		return new OptionSelectByIndex(index);
	}

	public static IOptionSelector bySelected(boolean selected) {
		return new OptionSelectBySelected(selected);
	}

	public static IOptionSelector byText(String text) {
		return new OptionSelectByText(text);
	}

	public static IOptionSelector byValue(String value) {
		return new OptionSelectByValue(value);
	}

	/**
	 * @throws UnsupportedOperationException if {@link #isMultiple()} returns <code>false</code>.
	 */
	public void deselectAll();

	public IOption getFirstSelected();

	/**
	 * Get a single option.
	 * 
	 * @param selector A selector which we assume identifies a single option.
	 * @return The single option.
	 * @throws IllegalArgumentException if the selector selects any number of options other than one.
	 * @see #getOptions(IOptionSelector)
	 */
	public default IOption getOption(IOptionSelector selector) {
		return HCollection.getOne(getOptions(selector));
	}

	public List<IOption> getOptions(IOptionSelector selector);

	public boolean isMultiple();

	@Override
	public IOperationBuilder<? extends ISelect> operation();
}
