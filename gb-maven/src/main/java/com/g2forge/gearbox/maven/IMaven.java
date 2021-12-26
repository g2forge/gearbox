package com.g2forge.gearbox.maven;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.gearbox.command.converter.dumb.ArgumentRenderer;
import com.g2forge.gearbox.command.converter.dumb.Command;
import com.g2forge.gearbox.command.converter.dumb.Flag;
import com.g2forge.gearbox.command.converter.dumb.HDumbCommandConverter;
import com.g2forge.gearbox.command.converter.dumb.IArgumentRenderer;
import com.g2forge.gearbox.command.converter.dumb.Named;
import com.g2forge.gearbox.command.converter.dumb.Working;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.proxy.method.ICommandInterface;

public interface IMaven extends ICommandInterface {
	public static class MavenCoordinatesArgumentRenderer implements IArgumentRenderer<MavenCoordinates> {
		@Override
		public List<String> render(IMethodArgument<MavenCoordinates> argument) {
			final String coordinates = argument.get().toString();
			return HDumbCommandConverter.computeString(argument, coordinates);
		}
	}

	public static class MavenProfilesArgumentRenderer implements IArgumentRenderer<List<String>> {
		@Override
		public List<String> render(IMethodArgument<List<String>> argument) {
			final List<String> profiles = argument.get();
			if (profiles == null || profiles.isEmpty()) return HCollection.emptyList();
			return HCollection.asList("-P" + profiles.stream().collect(Collectors.joining(",")));
		}
	}

	public static Path mvn = Paths.get("mvn");

	@Command({ "mvn", "dependency:copy" })
	public IProcess dependencyCopy(@Working Path path, @Flag("-B") boolean batch, @ArgumentRenderer(MavenCoordinatesArgumentRenderer.class) @Named("-Dartifact=") MavenCoordinates artifact, @Named("-DoutputDirectory=") Path outputDirectory);

	@Command({ "mvn", "help:effective-pom", "--non-recursive" })
	public Stream<String> effectivePOM(@Working Path path, @Flag("-B") boolean batch, @Named("-Doutput=") Path output);

	@Command({})
	public Stream<String> maven(@Working Path path, Path maven, @Flag("-B") boolean batch, String goal, @ArgumentRenderer(MavenProfilesArgumentRenderer.class) List<String> profiles);
}
