package com.g2forge.gearbox.issue.serdes;

import com.g2forge.gearbox.issue.IIssueType;

public interface IIssueFormatRW<Type extends IIssueType<Payload>, Payload, Serialized> extends IIssueFormatR_<Type, Payload, Serialized>, IIssueFormat_W<Type, Payload, Serialized> {}
