package com.g2forge.gearbox.argparse;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.test.HAssert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class TestArgumentParser {
	@Data
	@Builder(toBuilder = true)
	@AllArgsConstructor
	protected static class Array {
		protected final String[] strings;
	}

	@Data
	@Builder(toBuilder = true)
	@AllArgsConstructor
	protected static class Flag {
		@Parameter("--flag")
		protected final boolean flag;
	}

	@Data
	@Builder(toBuilder = true)
	@AllArgsConstructor
	protected static class Mixed {
		@Parameter("--flag")
		@ArgumentHelp("An optional flag")
		protected final boolean flag;

		@ArgumentHelp("A path")
		protected final Path path;
	}

	@Data
	@Builder(toBuilder = true)
	@AllArgsConstructor
	protected static class None {}

	@Data
	@Builder(toBuilder = true)
	@AllArgsConstructor
	protected static class Ordered {
		protected final String string;
	}

	@Data
	@Builder(toBuilder = true)
	protected static class Unannotated {
		@Parameter("--unannotated")
		protected final String unannotated;

		/**
		 * Manually created constructor, so that the {@link Parameter} annotation does NOT appear on the parameter, thereby triggering our runtime lint check.
		 * 
		 * @param unannotated A parameter which is unannotated, matching a field which is annotated.
		 */
		public Unannotated(String unannotated) {
			this.unannotated = unannotated;
		}
	}

	@Data
	@Builder(toBuilder = true)
	@AllArgsConstructor
	protected static class Unparseable {
		protected final Unparseable unparseable;
	}

	@Test
	public void array() {
		final Array actual = ArgumentParser.parse(Array.class, HCollection.asList("A,B"));
		HAssert.assertEquals(new Array(new String[] { "A", "B" }), actual);
	}

	@Test
	public void flagFalse() {
		final Flag actual = ArgumentParser.parse(Flag.class, HCollection.asList());
		HAssert.assertEquals(new Flag(false), actual);
	}

	@Test
	public void flagTrue() {
		final Flag actual = ArgumentParser.parse(Flag.class, HCollection.asList("--flag"));
		HAssert.assertEquals(new Flag(true), actual);
	}

	@Test
	public void missing() {
		HAssert.assertException(UnspecifiedParameterException.class, "Parameter #0 (string) was not specified!", () -> ArgumentParser.parse(Ordered.class, HCollection.asList()));
	}

	@Test
	public void mixed1() {
		final String expected = "path";
		final Mixed actual = ArgumentParser.parse(Mixed.class, HCollection.asList("--flag", expected));
		HAssert.assertEquals(new Mixed(true, Paths.get(expected)), actual);
	}

	@Test
	public void mixed2() {
		final String expected = "path";
		final Mixed actual = ArgumentParser.parse(Mixed.class, HCollection.asList(expected, "--flag"));
		HAssert.assertEquals(new Mixed(true, Paths.get(expected)), actual);
	}

	@Test
	public void none() {
		HAssert.assertInstanceOf(None.class, ArgumentParser.parse(None.class, HCollection.asList()));
	}

	@Test
	public void ordered() {
		final String expected = "value";
		final Ordered actual = ArgumentParser.parse(Ordered.class, HCollection.asList(expected));
		HAssert.assertEquals(new Ordered(expected), actual);
	}

	@Test
	public void unannotated() {
		HAssert.assertThrows(RuntimeException.class, () -> ArgumentParser.parse(Unannotated.class, HCollection.asList("--unannotated", "value")));
	}

	@Test
	public void unparseable() {
		HAssert.assertThrows(UnparseableArgumentException.class, () -> ArgumentParser.parse(Unparseable.class, HCollection.asList("argument")));
	}
}
