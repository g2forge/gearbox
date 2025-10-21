package com.g2forge.gearbox.issue.csv;

import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.g2forge.alexandria.java.core.error.DependencyNotLoadedError;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.ICloseableConsumer1;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueType;
import com.g2forge.gearbox.issue.Level;
import com.g2forge.gearbox.issue.sink.ICloseableIssueSink;
import com.g2forge.gearbox.csv.CSVMapper;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

public class TypedCSVIssueSink<Type extends IIssueType<Payload>, Payload> implements ICloseableIssueSink<Type> {
	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	protected static class LoggedIssue<Payload> {
		protected final Level level;

		protected final String code;

		protected final String description;

		@JsonUnwrapped
		protected final Payload payload;
	}

	protected static <Type extends IIssueType<Payload>, Payload> LoggedIssue<Payload> computeLoggedIssue(IIssue<Type, Payload> issue) {
		final Type type = issue.getType();
		return new LoggedIssue<>(type.getLevel(), type.getCode(), type.getDescription(), issue.getPayload());
	}

	protected final ITypeRef<Payload> payloadType;

	protected final ICloseableConsumer1<? super LoggedIssue<Payload>> writer;

	public TypedCSVIssueSink(Path path, ITypeRef<Payload> payloadType, String... payloadColumns) {
		this.payloadType = payloadType;
		final List<String> allColumns = HCollection.concatenate(HCollection.asList("level", "code", "description"), HCollection.asList(payloadColumns));
		this.writer = DependencyNotLoadedError.tryWithModule(() -> new CSVMapper<>(LoggedIssue.class, allColumns).write(path), HCSVIssueSink.MODULES);
	}

	@Override
	public void close() {
		writer.close();
	}

	@Override
	public void report(IIssue<? extends Type, ?> issue) {
		if (payloadType.isInstance(issue.getPayload())) {
			@SuppressWarnings("unchecked")
			final IIssue<Type, Payload> cast = (IIssue<Type, Payload>) issue;
			final LoggedIssue<Payload> computeLoggedIssue = computeLoggedIssue(cast);
			writer.accept(computeLoggedIssue);
		}
	}
}
