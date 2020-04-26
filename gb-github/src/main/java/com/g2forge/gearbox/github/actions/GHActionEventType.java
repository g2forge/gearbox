package com.g2forge.gearbox.github.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.g2forge.alexandria.java.text.casing.CamelCase;
import com.g2forge.alexandria.java.text.casing.SnakeCase;

public enum GHActionEventType {
	Published,
	Unpublished,
	Created,
	Edited,
	Deleted,
	Prereleased;

	@JsonCreator
	public static GHActionEventType parse(String string) {
		return valueOf(CamelCase.create().toString(new SnakeCase("_").fromString(string)));
	}

	@JsonValue
	@Override
	public String toString() {
		return new SnakeCase("_").toString(CamelCase.create().fromString(name()));
	}
}
