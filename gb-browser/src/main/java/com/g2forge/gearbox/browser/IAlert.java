package com.g2forge.gearbox.browser;

public interface IAlert {
	public void accept();

	public void dismiss();

	public String getText();

	public IAlert send(String text);
}
