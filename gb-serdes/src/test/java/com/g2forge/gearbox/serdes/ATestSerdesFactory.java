package com.g2forge.gearbox.serdes;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.g2forge.alexandria.java.close.ICloseableSupplier;
import com.g2forge.alexandria.java.core.resource.Resource;
import com.g2forge.alexandria.java.function.ICloseableConsumer1;
import com.g2forge.alexandria.java.io.dataaccess.ByteArrayDataSink;
import com.g2forge.alexandria.java.io.dataaccess.ResourceDataSource;
import com.g2forge.alexandria.java.io.serdes.ISerdesFactoryRW;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.alexandria.test.HAssert;

public abstract class ATestSerdesFactory<T> {
	protected abstract ISerdesFactoryRW<T> createSerdesFactory();

	protected abstract T fromString(String string);

	protected abstract SerdesFormat getFormat();

	protected Resource getResource() {
		return new Resource(getClass(), "Example." + getFormat().getMediaType().getFileExtensions().getDefaultExtension() + ".txt");
	}

	protected abstract ITypeRef<T> getTypeRef();

	@Test
	public void read() {
		final ResourceDataSource source = new ResourceDataSource(getResource());
		try (final ICloseableSupplier<? extends T> supplier = createSerdesFactory().create(source)) {
			HAssert.assertEquals("A", toString(supplier.get()));
			HAssert.assertEquals("B", toString(supplier.get()));
			HAssert.assertThrows(NoSuchElementException.class, () -> supplier.get());
		}
	}

	protected abstract String toString(T value);

	@Test
	public void write() {
		final ByteArrayDataSink sink = new ByteArrayDataSink();
		try (final ICloseableConsumer1<? super T> consumer = createSerdesFactory().create(sink)) {
			consumer.accept(fromString("A"));
			consumer.accept(fromString("B"));
		}
		HAssert.assertEquals(getResource(), sink.getStream().toString());;
	}
}
