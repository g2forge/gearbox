package com.g2forge.gearbox.serdes.injection;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ExampleObjectDeserializer extends StdDeserializer<ExampleObject> {
	private static final long serialVersionUID = 2867578675754843411L;

	public ExampleObjectDeserializer() {
		super(ExampleObject.class);
	}

	@Override
	public ExampleObject deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
		final JsonNode node = parser.getCodec().readTree(parser);
		final int value = node.get("value").asInt();
		return new ExampleObject(ExampleObject.PREPROCESSOR.get(context).apply(value));
	}
}
