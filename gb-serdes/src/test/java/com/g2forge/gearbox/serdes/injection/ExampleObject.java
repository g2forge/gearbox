package com.g2forge.gearbox.serdes.injection;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.type.ref.ATypeRef;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
@JsonSerialize(using = ExampleObjectSerializer.class)
@JsonDeserialize(using = ExampleObjectDeserializer.class)
public class ExampleObject {
	public static final JacksonInjectedValue<IFunction1<Integer, Integer>> PREPROCESSOR = new JacksonInjectedValue<>(ExampleObject.class, "preprocessor", new ATypeRef<IFunction1<Integer, Integer>>() {}, IFunction1.identity());

	protected final Integer value;
}
