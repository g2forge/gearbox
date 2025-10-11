package com.g2forge.gearbox.browser;

import java.util.List;

import org.openqa.selenium.By;

import com.g2forge.alexandria.annotations.note.Note;
import com.g2forge.alexandria.annotations.note.NoteType;
import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.gearbox.browser.operation.IOperationBuilder;

@Note(type = NoteType.TODO, value = "Separate browser from browser accessor, so that we don't get resource leak warnings")
public interface IBrowser extends ICloseable, IBrowsable {
	public Object executeJavascript(String script, Object... args);

	public IElement find(By by);

	public <T extends IElement> T find(By by, Class<T> type);

	public List<IElement> findAll(By by);

	public <T extends IElement> List<T> findAll(By by, Class<T> type);

	public IAlert getAlert();

	public String getTitle();

	public IBrowser go(String url);

	@Override
	public IOperationBuilder<? extends IBrowser> operation();

	public IBrowser refresh();
}
