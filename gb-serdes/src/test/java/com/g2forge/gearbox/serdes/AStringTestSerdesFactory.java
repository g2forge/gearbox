package com.g2forge.gearbox.serdes;

import com.g2forge.alexandria.java.io.serdes.ISerdesFactoryRW;
import com.g2forge.alexandria.java.type.ref.ITypeRef;

public abstract class AStringTestSerdesFactory extends ATestSerdesFactory<String> {
	@Override
	protected ISerdesFactoryRW<String> createSerdesFactory() {
		return getFormat().createSerdesFactory(getTypeRef());
	}

	@Override
	protected String fromString(String string) {
		return string;
	}

	@Override
	protected ITypeRef<String> getTypeRef() {
		return ITypeRef.of(String.class);
	}

	@Override
	protected String toString(String value) {
		return value;
	}
}
