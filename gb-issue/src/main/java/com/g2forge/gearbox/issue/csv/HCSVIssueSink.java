package com.g2forge.gearbox.issue.csv;

import com.g2forge.alexandria.java.core.marker.Helpers;

import lombok.experimental.UtilityClass;

@UtilityClass
@Helpers
public class HCSVIssueSink {
	protected static final String[] MODULES = { "gb-serdes", "jackson-dataformat-csv" };
}
