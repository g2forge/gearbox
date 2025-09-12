package com.g2forge.gearbox.jira.sprint;

import java.net.URI;

import org.joda.time.DateTime;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class Sprint {
	protected final URI self;

	protected final Long id;

	protected final String name;

	protected final DateTime startDate, endDate;
}