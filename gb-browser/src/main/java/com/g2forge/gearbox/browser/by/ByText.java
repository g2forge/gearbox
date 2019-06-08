package com.g2forge.gearbox.browser.by;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.g2forge.alexandria.java.adt.identity.IIdentity;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.function.IPredicate1;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ByText extends By {
	@RequiredArgsConstructor
	public static class Parser implements IIdentity<String> {
		protected final List<? extends IFunction1<? super String, ? extends String>> cleanups;

		@SafeVarargs
		public Parser(IFunction1<? super String, ? extends String>... cleanups) {
			this(HCollection.asList(cleanups));
		}

		public String cleanup(String string) {
			String retVal = string;
			for (IFunction1<? super String, ? extends String> cleanup : cleanups) {
				retVal = cleanup.apply(retVal);
			}
			return retVal;
		}

		@Override
		public boolean equals(String _this, Object that) {
			if (_this == that) return true;
			if (!(that instanceof String)) return false;
			return cleanup(_this).equals(cleanup((String) that));
		}

		@Override
		public int hashCode(String _this) {
			return cleanup(_this).hashCode();
		}
	}

	protected final By base;

	protected final IPredicate1<? super String> predicate;

	public ByText(By base, String text) {
		this(base, text, IIdentity.standard());
	}

	@SafeVarargs
	public ByText(By base, String text, IFunction1<? super String, ? extends String>... cleanups) {
		this(base, text, new Parser(cleanups));
	}

	public ByText(By base, String text, IIdentity<? super String> identity) {
		this.base = base;
		final IIdentity<? super String> actualIdentity = identity == null ? IIdentity.standard() : identity;
		this.predicate = string -> actualIdentity.equals(text, string);
	}

	@Override
	public List<WebElement> findElements(SearchContext context) {
		return base.findElements(context).stream().filter(element -> predicate.test(element.getText())).collect(Collectors.toList());
	}
}
