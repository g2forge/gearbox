package com.g2forge.gearbox.jira;

import org.slf4j.LoggerFactory;

import com.g2forge.alexandria.java.marker.Helpers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.experimental.UtilityClass;

@Helpers
@UtilityClass
public class HLogback {
	public static void setLogLevel(Level level) {
		((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(level);
	}
}
