package com.g2forge.gearbox.issue.serdes;

import com.g2forge.alexandria.java.function.ICloseableConsumer1;
import com.g2forge.alexandria.java.io.dataaccess.IDataSink;
import com.g2forge.alexandria.java.io.serdes.ISerdesFactory_W;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueType;
import com.g2forge.gearbox.issue.sink.ICloseableIssueSink;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Slf4j
public class SerializingIssueSink<Type extends IIssueType<Payload>, Payload, Serialized> implements ICloseableIssueSink<Type> {
	protected final ICloseableConsumer1<? super Serialized> sink;

	protected final IIssueFormat_W<Type, Payload, Serialized> issueFormat;

	protected final ITypeRef<Payload> payloadType;

	public SerializingIssueSink(ISerdesFactory_W<Serialized> serdesFactory, IDataSink sink, IIssueFormat_W<Type, Payload, Serialized> issueFormat, ITypeRef<Payload> payloadType) {
		this(serdesFactory.create(sink), issueFormat, payloadType);
	}

	@Override
	public void close() {
		getSink().close();
	}

	@Override
	public void report(IIssue<? extends Type, ?> issue) {
		if (getPayloadType().isInstance(issue.getPayload())) {
			@SuppressWarnings("unchecked")
			final IIssue<Type, Payload> cast = (IIssue<Type, Payload>) issue;
			getSink().accept(getIssueFormat().serialize(cast));
		} else log.warn("Issue with payload type {} cannot be written to {} with payload type {}", issue.getPayload().getClass().getSimpleName(), getClass().getSimpleName(), getPayloadType().getSimpleName());
	}
}
