package com.g2forge.gearbox.serdes.injection;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.g2forge.alexandria.test.HAssert;

public class TestInjection {
	@Test
	public void noinjection() throws JsonMappingException, JsonProcessingException {
		final ExampleObject actual = new ExampleObject(0);
		final ObjectMapper mapper = new ObjectMapper();
		HAssert.assertEquals(actual, mapper.readValue(mapper.writeValueAsString(actual), ExampleObject.class));
	}

	@Test
	public void increment() throws JsonMappingException, JsonProcessingException {
		final ObjectMapper mapper = ExampleObject.PREPROCESSOR.inject(new ObjectMapper(), x -> x + 1);;
		HAssert.assertEquals(new ExampleObject(2), mapper.readValue(mapper.writeValueAsString(new ExampleObject(0)), ExampleObject.class));
	}
}
