package com.g2forge.gearbox.maven.packaging;

public enum MavenPackaging implements IMavenPackaging {
	POM,
	JAR,
	MAVEN_PLUGIN {
		@Override
		public String toString() {
			return "MAVEN-PLUGIN";
		}
	},
	EJB,
	WAR,
	EAR,
	RAR,
	BUNDLE,
	ECLIPSE_PLUGIN {
		@Override
		public String toString() {
			return "ECLIPSE-PLUGIN";
		}
	};

	@Override
	public String getName() {
		return toString().toLowerCase();
	}
}