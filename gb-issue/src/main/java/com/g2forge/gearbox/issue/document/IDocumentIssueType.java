package com.g2forge.gearbox.issue.document;

import com.g2forge.enigma.document.model.IBlock;
import com.g2forge.enigma.document.model.ISpan;
import com.g2forge.enigma.document.model.Text;
import com.g2forge.gearbox.issue.IIssueType;

public interface IDocumentIssueType<Payload> extends IIssueType<Payload> {
	public default IBlock computeDocument(Payload payload) {
		return computeSummary(payload);
	}

	public default ISpan computeSummary(Payload payload) {
		return new Text(computeMessage(payload));
	}
}
