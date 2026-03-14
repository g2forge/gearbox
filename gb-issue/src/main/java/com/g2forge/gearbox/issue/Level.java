package com.g2forge.gearbox.issue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Level {
	DEBUG(org.slf4j.event.Level.DEBUG),
	INFO(org.slf4j.event.Level.INFO),
	WARN(org.slf4j.event.Level.WARN),
	ERROR(org.slf4j.event.Level.ERROR);

	public static final Level MINIMUM = Level.values()[0];

	public static final Level MAXIMUM = Level.values()[Level.values().length - 1];

	protected final org.slf4j.event.Level slf4j;
}
