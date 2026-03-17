package com.g2forge.gearbox.serdes.csv;

import com.g2forge.alexandria.java.close.ICloseableSupplier;
import com.g2forge.alexandria.java.function.ICloseableConsumer1;
import com.g2forge.alexandria.java.io.dataaccess.IDataSink;
import com.g2forge.alexandria.java.io.dataaccess.IDataSource;
import com.g2forge.alexandria.java.io.serdes.ISerdesFactoryRW;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class CSVSerdesFactory<T> implements ISerdesFactoryRW<T> {
	protected final ICSVMapper<T> mapper;

	@Override
	public ICloseableSupplier<T> create(IDataSource source) {
		return getMapper().read(source);

	}
	@Override
	public ICloseableConsumer1<? super T> create(IDataSink sink) {
		return getMapper().write(sink);
	}
}
