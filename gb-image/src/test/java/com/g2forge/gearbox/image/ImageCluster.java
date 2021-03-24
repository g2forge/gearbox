package com.g2forge.gearbox.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.g2forge.alexandria.adt.associative.map.HMap;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.io.file.HFile;
import com.g2forge.alexandria.media.MediaType;
import com.g2forge.gearbox.image.PHashImageComparator.CharacterizedImage;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

public class ImageCluster {
	@Data
	@Builder
	public static class Cluster {
		@Singular
		protected final Map<Path, CharacterizedImage> images;

		@Singular
		protected final List<Cluster> clusters;

		public int distance(PHashImageComparator comparator, Cluster other) {
			int retVal = Integer.MAX_VALUE;
			for (CharacterizedImage i0 : getImages().values()) {
				for (CharacterizedImage i1 : other.getImages().values()) {
					retVal = Math.min(retVal, comparator.distance(i0, i1));
				}
			}
			return retVal;
		}

		public Cluster join(Cluster other) {
			return new Cluster(HMap.merge(getImages(), other.getImages()), Arrays.asList(this, other));
		}
	}

	public static void main(String[] args) throws IOException {
		final PHashImageComparator comparator = new PHashImageComparator(16);
		final Map<Path, CharacterizedImage> characterized = HFile.toList(Files.newDirectoryStream(Paths.get(args[0]))).stream().filter(p -> MediaType.getRegistry().isMediaType(MediaType.JPG, p)).collect(Collectors.toMap(IFunction1.identity(), comparator::characterize));
		final List<Cluster> clusters = new ArrayList<>(characterized.entrySet().stream().map(e -> Cluster.builder().image(e.getKey(), e.getValue()).build()).collect(Collectors.toList()));
		while (clusters.size() > 1) {
			int d0 = Integer.MAX_VALUE, i0 = -1, j0 = -1;
			for (int i = 1; i < clusters.size(); i++) {
				for (int j = 0; j < i; j++) {
					final int d = clusters.get(i).distance(comparator, clusters.get(j));
					if (d < d0) {
						d0 = d;
						i0 = i;
						j0 = j;
					}
				}
			}
			clusters.add(clusters.remove(i0).join(clusters.remove(j0)));
		}

		print(0, HCollection.getOne(clusters));
	}

	public static void print(int indent, Cluster cluster) {
		for (int i = 0; i < indent; i++) {
			System.out.print("  ");
		}
		System.out.println("Cluster: " + cluster.getImages().keySet().stream().map(ci -> ci.getFileName().toString()).collect(Collectors.joining(", ")));
		for (Cluster child : cluster.getClusters()) {
			print(indent + 1, child);
		}
	}
}
