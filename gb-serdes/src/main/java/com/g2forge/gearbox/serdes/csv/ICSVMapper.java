package com.g2forge.gearbox.serdes.csv;

import java.util.Collection;
import java.util.List;

import com.g2forge.alexandria.java.close.ICloseableSupplier;
import com.g2forge.alexandria.java.function.ICloseableConsumer1;
import com.g2forge.alexandria.java.io.dataaccess.IDataSink;
import com.g2forge.alexandria.java.io.dataaccess.IDataSource;

public interface ICSVMapper<T> {
	public List<T> readAll(IDataSource source);

	public ICloseableSupplier<T> read(IDataSource source);

	public String write(Collection<T> values);

	public void write(Collection<T> values, IDataSink sink);

	public ICloseableConsumer1<? super T> write(IDataSink sink);
}
