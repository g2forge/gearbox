package com.g2forge.gearbox.serdes;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.g2forge.alexandria.java.close.ICloseableSupplier;
import com.g2forge.alexandria.java.core.resource.Resource;
import com.g2forge.alexandria.java.function.ICloseableConsumer1;
import com.g2forge.alexandria.java.io.dataaccess.ByteArrayDataSink;
import com.g2forge.alexandria.java.io.dataaccess.ResourceDataSource;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.alexandria.test.HAssert;

public abstract class ATestSerdesFactory {
	protected abstract SerdesFormat getFormat();

	protected Resource getResource() {
		return new Resource(getClass(), "Example." + getFormat().getMediaType().getFileExtensions().getDefaultExtension() + ".txt");
	}

	@Test
	public void read() {
		final ResourceDataSource source = new ResourceDataSource(getResource());
		try (final ICloseableSupplier<String> supplier = getFormat().createSerdesFactory(ITypeRef.of(String.class)).create(source)) {
			HAssert.assertEquals("A", supplier.get());
			HAssert.assertEquals("B", supplier.get());
			HAssert.assertThrows(NoSuchElementException.class, () -> supplier.get());
		}
	}

	@Test
	public void write() {
		final ByteArrayDataSink sink = new ByteArrayDataSink();
		try (final ICloseableConsumer1<String> consumer = getFormat().createSerdesFactory(ITypeRef.of(String.class)).create(sink)) {
			consumer.accept("A");
			consumer.accept("B");
		}
		HAssert.assertEquals(getResource(), sink.getStream().toString());;
	}
}
