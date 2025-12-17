package com.g2forge.gearbox.serdes.injection;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.exc.MissingInjectableValueExcepion;
import com.g2forge.alexandria.java.type.ref.ITypeRef;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class JacksonInjectedValue<T> {
	protected final String id;

	protected final ITypeRef<T> type;

	protected final T fallback;

	public JacksonInjectedValue(Class<?> owner, String name, ITypeRef<T> type, T fallback) {
		this(owner.getName() + "." + name, type, fallback);
	}

	public JacksonInjectedValue(Class<T> type, T fallback) {
		this(type.getName(), ITypeRef.of(type), fallback);
	}

	public ObjectMapper inject(ObjectMapper mapper, T value) {
		final SerializationConfig initialSerializationConfig = mapper.getSerializationConfig();
		final ContextAttributes attributes = initialSerializationConfig.getAttributes();
		final ContextAttributes attributesWithInjectedValue = attributes.withSharedAttribute(getId(), value);
		final SerializationConfig serializationConfigWithInjectedValue = initialSerializationConfig.with(attributesWithInjectedValue);

		final InjectableValues existingInjectableValues = mapper.getInjectableValues();
		if (existingInjectableValues == null) mapper.setInjectableValues(new InjectableValues.Std().addValue(getId(), value));
		else if (!(existingInjectableValues instanceof InjectableValues.Std)) throw new IllegalArgumentException();
		else((InjectableValues.Std) existingInjectableValues).addValue(getId(), value);

		return mapper.setConfig(serializationConfigWithInjectedValue);
	}

	public T get(SerializerProvider provider) {
		final Object value = provider.getAttribute(getId());
		if (value == null) return getFallback();
		return getType().cast(value);
	}

	public T get(DeserializationContext context) {
		final Object value;
		try {
			value = context.findInjectableValue(getId(), null, null, null, null);
		} catch (MissingInjectableValueExcepion e) {
			return getFallback();
		} catch (JsonMappingException e) {
			throw new IllegalStateException(e);
		}
		if (value == null) return getFallback();
		return getType().cast(value);
	}
}
