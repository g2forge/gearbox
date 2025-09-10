package com.g2forge.gearbox.issue.document;

import com.g2forge.enigma.document.model.DocList;
import com.g2forge.enigma.document.model.DocList.Marker;
import com.g2forge.enigma.document.model.IBlock;
import com.g2forge.gearbox.issue.DependencyNotLoadedError;
import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueSink;

public class DocumentIssueSink<Type extends IDocumentIssueType<?>> implements IIssueSink<Type> {
	protected final DocList.DocListBuilder list;

	public DocumentIssueSink() {
		try {
			list = DocList.builder().marker(Marker.Numbered);
		} catch (NoClassDefFoundError error) {
			throw new DependencyNotLoadedError("en-document", error);
		}
	}

	public IBlock build() {
		return list.build();
	}

	@Override
	public void report(IIssue<? extends Type, ?> issue) {
		IDocumentIssueConsumer.create(this::reportInternal).accept(issue);
	}

	protected <_Type extends IDocumentIssueType<_Payload>, _Payload> void reportInternal(IIssue<_Type, _Payload> issue) {
		list.item(issue.getType().computeDocument(issue.getPayload()));
	}
}
