package com.g2forge.gearbox.image;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.g2forge.alexandria.java.adt.compare.ComparableComparator;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.io.file.HFile;
import com.g2forge.alexandria.media.MediaType;

public class CompareImages {
	protected static <T> void compareImages(final Path path, final IImageComparator<T> comparator, final PrintStream out) throws IOException {
		final List<Path> paths = HFile.toList(Files.newDirectoryStream(path));
		final Map<Path, T> characterized = paths.stream().filter(p -> MediaType.getRegistry().isMediaType(MediaType.JPG, p)).collect(Collectors.toMap(IFunction1.identity(), comparator::characterize));
		final int length = characterized.keySet().stream().map(p -> p.getFileName().toString().length()).max(ComparableComparator.create()).get();

		for (int i = 0; i < characterized.size(); i++) {
			final Path pi = paths.get(i);
			final T ci = characterized.get(pi);
			{
				final String string = pi.getFileName().toString();
				out.print(string);
				for (int k = string.length(); k < length; k++) {
					out.print(' ');
				}
			}

			for (int j = 0; j <= i; j++) {
				final Path pj = paths.get(j);
				final T cj = characterized.get(pj);
				out.print(' ');
				final String string = Integer.toString(comparator.distance(ci, cj));
				for (int k = string.length(); k < 4; k++) {
					out.print(' ');
				}
				out.print(string);
			}
			out.println();
		}
	}

	public static void main(String[] args) throws IOException {
		compareImages(Paths.get(args[0]), new PHashImageComparator(16), System.out);
	}
}
