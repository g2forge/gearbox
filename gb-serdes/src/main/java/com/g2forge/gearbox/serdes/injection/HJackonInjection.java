package com.g2forge.gearbox.serdes.injection;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;

public class HJackonInjection {
	public ObjectMapper with(ObjectMapper mapper, Map<String, ?> injectables) {
		final SerializationConfig initialSerializationConfig = mapper.getSerializationConfig();
		final ContextAttributes attributes = initialSerializationConfig.getAttributes();
		final ContextAttributes attributesWithInjectedValue = attributes.withSharedAttributes(injectables);
		final SerializationConfig serializationConfigWithInjectedValue = initialSerializationConfig.with(attributesWithInjectedValue);
		
		mapper.setInjectableValues(null);
		
		return mapper.setConfig(serializationConfigWithInjectedValue);
	}
}
