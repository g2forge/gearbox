package com.g2forge.gearbox.command.proxy.result;

import java.util.stream.Stream;

import com.g2forge.alexandria.java.concurrent.AThreadActor;
import com.g2forge.alexandria.java.function.IConsumer1;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StreamConsumer extends AThreadActor {
	protected final Stream<String> stream;

	protected final IConsumer1<String> consumer;

	@Override
	protected void run() {
		final RuntimeException closed = new RuntimeException();
		try {
			stream.forEach(line -> {
				consumer.accept(line);
				if (!isOpen()) throw closed;
			});
		} catch (RuntimeException exception) {
			if (exception != closed) throw exception;
		}
	}

	@Override
	public StreamConsumer open() {
		return (StreamConsumer) super.open();
	}

	@Override
	protected void shutdown() {
		stream.close();
	}
}