package com.g2forge.gearbox.image;

import java.nio.file.Path;

public interface IImageComparator<T> {
	public T characterize(Path path);

	/**
	 * Measure the distance between two images, and produce a score such that a lower score indicates the images are more similar. This method should return
	 * <code>0</code> if and only if the images are identical according this to comparator's metric.
	 * 
	 * @param image0 An image to compare
	 * @param image1 An image to compare
	 * @return A score of image similarity, from 0 to {@link Integer#MAX_VALUE}, where smaller scores indicate more similar images.
	 */
	public int distance(T image0, T image1);
}
