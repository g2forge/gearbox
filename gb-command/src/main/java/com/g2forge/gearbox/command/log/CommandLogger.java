package com.g2forge.gearbox.command.log;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.java.io.RuntimeIOException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandLogger implements ICloseable, IConsumer1<String> {
	protected final Path logFile;

	protected final IConsumer1<String> tee;

	protected PrintStream stream = null;

	@Override
	public void accept(String t) {
		if (stream == null) try {
			Files.createDirectories(logFile.getParent());
			stream = new PrintStream(Files.newOutputStream(logFile));
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
		stream.println(t);
		if (tee != null) tee.accept(t);
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