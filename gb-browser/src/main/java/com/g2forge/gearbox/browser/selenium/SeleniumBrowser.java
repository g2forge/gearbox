package com.g2forge.gearbox.browser.selenium;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.browser.IAlert;
import com.g2forge.gearbox.browser.IBrowser;
import com.g2forge.gearbox.browser.IElement;
import com.g2forge.gearbox.browser.IForm;
import com.g2forge.gearbox.browser.ISelect;
import com.g2forge.gearbox.browser.operation.IOperationBuilder;

public class SeleniumBrowser implements IBrowser {
	protected final WebDriver driver;

	protected boolean open = true;

	public SeleniumBrowser() {
		final FirefoxOptions options = new FirefoxOptions();
		options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
		this.driver = new FirefoxDriver(options);
	}

	protected void assertOpen() {
		if (!open) throw new IllegalStateException();
	}

	public void close() {
		assertOpen();
		open = false;
		driver.close();
	}

	protected <T extends IElement> T convert(final WebElement element, Class<T> type) {
		final Object retVal;
		if (IElement.class.equals(type)) retVal = new Element(element, this);
		else if (IForm.class.equals(type)) retVal = new Form(element, this);
		else if (ISelect.class.equals(type)) retVal = new Select(element, this);
		else throw new IllegalArgumentException(type.toString());

		@SuppressWarnings("unchecked")
		final T cast = (T) retVal;
		return cast;
	}

	@Override
	public Object executeJavascript(String script, Object... args) {
		return ((JavascriptExecutor) driver).executeScript(script, args);
	}

	public IElement find(By by) {
		return find(by, IElement.class);
	}

	@Override
	public <T extends IElement> T find(By by, Class<T> type) {
		return convert(driver.findElement(by), type);
	}

	@Override
	public List<IElement> findAll(By by) {
		return findAll(by, IElement.class);
	}

	@Override
	public <T extends IElement> List<T> findAll(By by, Class<T> type) {
		final List<WebElement> elements = driver.findElements(by);
		return elements.stream().map(e -> convert(e, type)).collect(Collectors.toList());
	}

	@Override
	public IAlert getAlert() {
		final Alert alert;
		try {
			alert = driver.switchTo().alert();
		} catch (NoAlertPresentException e) {
			return null;
		}
		return new IAlert() {
			@Override
			public void accept() {
				alert.accept();
			}

			@Override
			public void dismiss() {
				alert.dismiss();
			}

			@Override
			public String getText() {
				return alert.getText();
			}

			@Override
			public IAlert send(String text) {
				alert.sendKeys(text);
				return this;
			}
		};
	}

	@Override
	public IBrowser getBrowser() {
		return this;
	}

	@Override
	public String getTitle() {
		return driver.getTitle();
	}

	@Override
	public IBrowser go(String url) {
		driver.get(url);
		return this;
	}

	@Override
	public IOperationBuilder<? extends IBrowser> operation() {
		return new IOperationBuilder<IBrowser>() {
			@Override
			public IBrowser execute(IConsumer1<? super IBrowser> operation) {
				operation.accept(SeleniumBrowser.this);
				return SeleniumBrowser.this;
			}

			@Override
			public <V> V until(IFunction1<? super IBrowser, ? extends V> function) {
				return new WebDriverWait(driver, 30).until(d -> function.apply(SeleniumBrowser.this));
			}

			@Override
			public <V> V until(int seconds, IFunction1<? super IBrowser, ? extends V> function) {
				return new WebDriverWait(driver, seconds).until(d -> function.apply(SeleniumBrowser.this));
			}
		};
	}

	@Override
	public IBrowser refresh() {
		driver.navigate().refresh();
		return this;
	}
}
