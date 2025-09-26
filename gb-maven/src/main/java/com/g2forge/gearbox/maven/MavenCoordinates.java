package com.g2forge.gearbox.maven;

import com.g2forge.alexandria.java.fluent.optional.NullableOptional;
import com.g2forge.gearbox.maven.packaging.IMavenPackaging;
import com.g2forge.gearbox.maven.packaging.MavenPackaging;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class MavenCoordinates {
	protected final String groupId;

	protected final String artifactId;

	protected final String version;

	@Builder.Default
	protected final IMavenPackaging packaging = MavenPackaging.JAR;

	@ToString.Exclude
	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private final String versionLowercase = NullableOptional.ofNullable(getVersion()).map(String::toLowerCase).or(null);

	@EqualsAndHashCode.Include(replaces = "version")
	protected String getVersionIdentity() {
		return getVersionLowercase();
	}

	@Override
	public String toString() {
		final StringBuilder retVal = new StringBuilder();
		retVal.append(getGroupId()).append(':').append(getArtifactId()).append(':').append(getVersion());
		if (getPackaging() != null) retVal.append(':').append(getPackaging().toString().toLowerCase());
		return retVal.toString();
	}
}
