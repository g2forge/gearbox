package com.g2forge.gearbox.issue.serdes;

import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueType;

public interface IIssueFormatR_<Type extends IIssueType<Payload>, Payload, Serialized> extends IIssueFormat__<Payload, Serialized> {
	public IIssue<Type, Payload> deserialize(Serialized serialized);
}
