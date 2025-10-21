package com.g2forge.gearbox.serdes.csv;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import com.g2forge.alexandria.java.function.ICloseableConsumer1;

public interface ICSVMapper<T> {
	public List<T> read(InputStream stream);

	public List<T> read(Path path);

	public List<T> read(String string);

	public String write(Collection<T> values);

	public void write(Collection<T> values, OutputStream stream);

	public void write(Collection<T> values, Path path);
	
	public ICloseableConsumer1<? super T> write(Path path);
}
