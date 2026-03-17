package com.g2forge.gearbox.serdes.injection;

import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MissingInjectableValueExcepion;
import com.g2forge.habitat.inject.IInjectedValue;
import com.g2forge.habitat.inject.InjectedValueDescriptor;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class JacksonInjectedValue<T> implements IInjectedValue<T> {
	protected final InjectedValueDescriptor<T> descriptor;

	public T get(DatabindContext context) {
		final Object value = context.getAttribute(getDescriptor().getId());
		if (value == null) return getDescriptor().getFallback();
		return getDescriptor().getType().cast(value);
	}

	public T get(ObjectMapper mapper) {
		final Object value;
		try {
			value = mapper.getInjectableValues().findInjectableValue(mapper.getDeserializationContext(), getDescriptor().getId(), null, null, null, null);
		} catch (MissingInjectableValueExcepion e) {
			return getDescriptor().getFallback();
		} catch (JsonMappingException e) {
			throw new IllegalStateException(e);
		}
		if (value == null) return getDescriptor().getFallback();
		return getDescriptor().getType().cast(value);
	}

	public ObjectMapper inject(ObjectMapper mapper, T value) {
		mapper.setConfig(mapper.getSerializationConfig().withAttribute(getDescriptor().getId(), value));
		mapper.setConfig(mapper.getDeserializationConfig().withAttribute(getDescriptor().getId(), value));

		final InjectableValues existingInjectableValues = mapper.getInjectableValues();
		if (existingInjectableValues == null) mapper.setInjectableValues(new InjectableValues.Std().addValue(getDescriptor().getId(), value));
		else if (!(existingInjectableValues instanceof InjectableValues.Std)) throw new IllegalArgumentException();
		else((InjectableValues.Std) existingInjectableValues).addValue(getDescriptor().getId(), value);

		return mapper;
	}
}
