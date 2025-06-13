package com.g2forge.gearbox.csv;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface ICSVMapper<T> {
	public List<T> read(InputStream stream);

	public List<T> read(Path path);

	public List<T> read(String string);

	public String write(Collection<T> values);

	public void write(Collection<T> values, OutputStream stream);

	public void write(Collection<T> values, Path path);
}
