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

	protected final org.slf4j.event.Level slf4j;
}
