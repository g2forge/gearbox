package com.g2forge.gearbox.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.helpers.HCollector;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.io.file.HFile;
import com.g2forge.alexandria.media.MediaType;
import com.g2forge.gearbox.image.PHashImageComparator.CharacterizedImage;

public class ClusterImages {
	public static void main(String[] args) throws IOException {
		final PHashImageComparator comparator = new PHashImageComparator(16);
		final Map<CharacterizedImage, Path> characterized = HFile.toList(Files.newDirectoryStream(Paths.get(args[0]))).stream().filter(p -> MediaType.getRegistry().isMediaType(MediaType.JPG, p)).collect(Collectors.toMap(comparator::characterize, IFunction1.identity(), HCollector.mergeFail(), IdentityHashMap::new));
		final List<ImageCluster<CharacterizedImage>> clusters = ImageCluster.cluster(comparator, characterized.keySet(), Integer.MAX_VALUE);
		print(0, HCollection.getOne(clusters), characterized);
	}

	public static void print(int indent, ImageCluster<CharacterizedImage> cluster, Map<CharacterizedImage, Path> characterized) {
		for (int i = 0; i < indent; i++) {
			System.out.print("  ");
		}
		System.out.println("Cluster: " + cluster.getImages().stream().map(characterized::get).map(ci -> ci.getFileName().toString()).collect(Collectors.joining(", ")));
		for (ImageCluster<CharacterizedImage> child : cluster.getClusters()) {
			print(indent + 1, child, characterized);
		}
	}
}
