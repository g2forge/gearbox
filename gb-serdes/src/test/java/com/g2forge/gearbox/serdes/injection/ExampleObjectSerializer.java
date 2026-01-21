package com.g2forge.gearbox.serdes.injection;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ExampleObjectSerializer extends StdSerializer<ExampleObject> {
	private static final long serialVersionUID = 7620126831613439913L;

	public ExampleObjectSerializer() {
		super(ExampleObject.class);
	}

	@Override
	public void serialize(ExampleObject value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException, JsonProcessingException {
		jsonGenerator.writeStartObject();
		jsonGenerator.writeNumberField("value", ExampleObject.PREPROCESSOR.get(provider).apply(value.getValue()));
		jsonGenerator.writeEndObject();
	}
}
