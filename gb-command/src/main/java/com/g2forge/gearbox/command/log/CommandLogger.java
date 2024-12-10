package com.g2forge.gearbox.command.log;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.java.io.RuntimeIOException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandLogger implements ICloseable, IConsumer1<String> {
	protected final Path logFile;

	protected final Collection<? extends IConsumer1<? super String>> tees;

	protected PrintStream stream = null;

	@SafeVarargs
	public CommandLogger(Path logFile, IConsumer1<? super String>... tees) {
		this(logFile, tees == null ? null : HCollection.asList(tees));
	}

	@Override
	public void accept(String t) {
		if (stream == null) try {
			Files.createDirectories(logFile.getParent());
			stream = new PrintStream(Files.newOutputStream(logFile));
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
		stream.println(t);
		if (tees != null) for (IConsumer1<? super String> tee : tees) {
			tee.accept(t);
		}
	}

	@Override
	public void close() {
		if (stream == null) {
			try {
				Files.deleteIfExists(logFile);
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
		} else stream.close();
	}
}