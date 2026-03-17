package com.g2forge.gearbox.issue.serdes;

import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueType;

public interface IIssueFormat_W<Type extends IIssueType<Payload>, Payload, Serialized> extends IIssueFormat__<Payload, Serialized> {
	public Serialized serialize(IIssue<Type, Payload> issue);
}
