package com.g2forge.gearbox.issue.serdes;

import java.util.NoSuchElementException;

import com.g2forge.alexandria.java.close.ICloseableSupplier;
import com.g2forge.alexandria.java.io.dataaccess.IDataSource;
import com.g2forge.alexandria.java.io.serdes.ISerdesFactoryR_;
import com.g2forge.gearbox.issue.IIssueSink;
import com.g2forge.gearbox.issue.IIssueType;
import com.g2forge.gearbox.issue.source.ICloseableIssueSource;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class DeserializingIssueSource<Type extends IIssueType<Payload>, Payload, Serialized> implements ICloseableIssueSource<Type> {
	protected final ICloseableSupplier<? extends Serialized> source;

	protected final IIssueFormatR_<Type, Payload, Serialized> issueFormat;

	public DeserializingIssueSource(ISerdesFactoryR_<Serialized> serdesFactory, IDataSource source, IIssueFormatR_<Type, Payload, Serialized> issueFormat) {
		this(serdesFactory.create(source), issueFormat);
	}

	@Override
	public void close() {
		getSource().close();
	}

	@Override
	public void send(IIssueSink<? super Type> sink) {
		do {
			final Serialized serialized;
			try {
				serialized = getSource().get();
			} catch (NoSuchElementException exception) {
				break;
			}
			sink.report(getIssueFormat().deserialize(serialized));
		} while (true);
	}
}
