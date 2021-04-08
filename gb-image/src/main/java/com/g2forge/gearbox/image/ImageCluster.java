package com.g2forge.gearbox.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.g2forge.alexandria.java.core.helpers.HCollection;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class ImageCluster<T> {
	/**
	 * Cluster the specified images using the comparator. Images (and clusters) whose distance is larger than {@code maxDistance} will not be clustered.
	 * 
	 * @param <T> The type of the characterized images.
	 * @param comparator An image comparator.
	 * @param images A collection of characterized images.
	 * @param maxDistance The maximum distance allowed between images (or clusters) to be merged. This can be set to {@code Integer.MAX_VALUE} to generate one
	 *            cluster.
	 * @return
	 */
	public static <T> List<ImageCluster<T>> cluster(final IImageComparator<? super T> comparator, final Collection<? extends T> images, int maxDistance) {
		if (maxDistance < 0) throw new IllegalArgumentException();
		final List<ImageCluster<T>> clusters = new ArrayList<>(images.stream().map(c -> ImageCluster.<T>builder().image(c).build()).collect(Collectors.toList()));
		while (clusters.size() > 1) {
			// Find the two closest clusters
			int d0 = maxDistance, i0 = -1, j0 = -1;
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
			// If no two clusters were closer than the max distance, then we're done
			if ((i0 < 0) || (j0 < 0)) break;
			// Merge them together
			clusters.add(clusters.remove(i0).join(clusters.remove(j0)));
		}
		return clusters;
	}

	@Singular
	protected final List<T> images;

	@Singular
	protected final List<ImageCluster<T>> clusters;

	public int distance(IImageComparator<? super T> comparator, ImageCluster<T> other) {
		int retVal = Integer.MAX_VALUE;
		for (T i0 : getImages()) {
			for (T i1 : other.getImages()) {
				retVal = Math.min(retVal, comparator.distance(i0, i1));
			}
		}
		return retVal;
	}

	public ImageCluster<T> join(ImageCluster<T> other) {
		return new ImageCluster<>(HCollection.concatenate(getImages(), other.getImages()), Arrays.asList(this, other));
	}
}