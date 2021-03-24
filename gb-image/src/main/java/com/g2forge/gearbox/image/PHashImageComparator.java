package com.g2forge.gearbox.image;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import com.g2forge.alexandria.java.core.helpers.HBinary;
import com.g2forge.alexandria.java.core.math.HMath;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.twelvemonkeys.image.ResampleOp;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class PHashImageComparator implements IImageComparator<PHashImageComparator.CharacterizedImage> {
	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	public static class CharacterizedImage {
		protected final long[] character;

		public CharacterizedImage(String hex) {
			this.character = HBinary.toLongs(HBinary.fromHex(hex));
		}

		@Override
		public String toString() {
			return HBinary.toHex(HBinary.toBytes(getCharacter()));
		}
	}

	protected final int size;

	@Override
	public CharacterizedImage characterize(final Path path) {
		final BufferedImage image;
		try (final InputStream input = Files.newInputStream(path)) {
			image = ImageIO.read(input);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
		final BufferedImage scaled = new ResampleOp(size, size, ResampleOp.FILTER_LANCZOS).filter(image, null);
		final BufferedImage gray = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(scaled, null);

		final int[] array = gray.getRGB(0, 0, size, size, new int[size * size], 0, size);
		double sum = 0.0;
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i] & 0xFF;
			sum += array[i];
		}
		final int average = (int) (sum / array.length);

		final long[] retVal = new long[HMath.divideCeiling(size * size, Long.SIZE)];
		for (int i = 0; i < array.length; i++) {
			if (array[i] >= average) retVal[i / Long.SIZE] |= 0x1l << (i % Long.SIZE);
		}
		return new CharacterizedImage(retVal);
	}

	@Override
	public int distance(CharacterizedImage image0, CharacterizedImage image1) {
		final long[] v0 = image0.getCharacter();
		final long[] v1 = image1.getCharacter();
		if (v0.length != v1.length) throw new IllegalArgumentException();

		int retVal = 0;
		for (int i = 0; i < v0.length; i++) {
			retVal += HMath.popCount(v0[i] ^ v1[i]);
		}
		return retVal;
	}
}
