package com.g2forge.gearbox.maven.packaging;

public enum MavenPackaging implements IMavenPackaging {
	POM,
	JAR,
	MAVEN_PLUGIN,
	MAVEN_ARCHETYPE,
	EJB,
	WAR,
	EAR,
	RAR,
	BUNDLE,
	ECLIPSE_PLUGIN,
	ATLASSIAN_PLUGIN;

	@Override
	public String getName() {
		return toString().replace("_", "-").toLowerCase();
	}
}