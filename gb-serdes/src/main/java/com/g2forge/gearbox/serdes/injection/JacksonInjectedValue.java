package com.g2forge.gearbox.serdes.injection;

import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	public T get(DatabindContext context) {
		final Object value = context.getAttribute(getId());
		if (value == null) return getFallback();
		return getType().cast(value);
	}

	public T get(ObjectMapper mapper) {
		final Object value;
		try {
			value = mapper.getInjectableValues().findInjectableValue(mapper.getDeserializationContext(), getId(), null, null, null, null);
		} catch (MissingInjectableValueExcepion e) {
			return getFallback();
		} catch (JsonMappingException e) {
			throw new IllegalStateException(e);
		}
		if (value == null) return getFallback();
		return getType().cast(value);
	}

	public ObjectMapper inject(ObjectMapper mapper, T value) {
		mapper.setConfig(mapper.getSerializationConfig().withAttribute(getId(), value));
		mapper.setConfig(mapper.getDeserializationConfig().withAttribute(getId(), value));

		final InjectableValues existingInjectableValues = mapper.getInjectableValues();
		if (existingInjectableValues == null) mapper.setInjectableValues(new InjectableValues.Std().addValue(getId(), value));
		else if (!(existingInjectableValues instanceof InjectableValues.Std)) throw new IllegalArgumentException();
		else((InjectableValues.Std) existingInjectableValues).addValue(getId(), value);

		return mapper;
	}
}
