package com.g2forge.gearbox.issue.csv;

import java.nio.file.Path;

import com.g2forge.alexandria.java.core.error.DependencyNotLoadedError;
import com.g2forge.alexandria.java.function.ICloseableConsumer1;
import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueFunction;
import com.g2forge.gearbox.issue.IIssueType;
import com.g2forge.gearbox.issue.Level;
import com.g2forge.gearbox.issue.sink.ICloseableIssueSink;
import com.g2forge.gearbox.csv.CSVMapper;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

public class GeneralCSVIssueSink<Type extends IIssueType<?>> implements ICloseableIssueSink<Type> {
	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	protected static class LoggedIssue {
		protected final Level level;

		protected final String code;

		protected final String message;
	}

	protected static <Type extends IIssueType<Payload>, Payload> LoggedIssue computeLoggedIssue(IIssue<Type, Payload> issue) {
		final Type type = issue.getType();
		return new LoggedIssue(type.getLevel(), type.getCode(), type.computeMessage(issue.getPayload()));
	}

	protected final ICloseableConsumer1<? super LoggedIssue> writer;

	public GeneralCSVIssueSink(Path path) {
		this.writer = DependencyNotLoadedError.tryWithModule(() -> new CSVMapper<>(LoggedIssue.class, "level", "code", "message").write(path), HCSVIssueSink.MODULES);

	}

	@Override
	public void close() {
		writer.close();
	}

	@Override
	public void report(IIssue<? extends Type, ?> issue) {
		final LoggedIssue loggedIssue = IIssueFunction.create(GeneralCSVIssueSink::computeLoggedIssue).apply(issue);
		writer.accept(loggedIssue);
	}
}
