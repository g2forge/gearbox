package com.g2forge.gearbox.issue.sink;

import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.gearbox.issue.IIssueSink;
import com.g2forge.gearbox.issue.IIssueType;

public interface ICloseableIssueSink<Type extends IIssueType<?>> extends IIssueSink<Type>, ICloseable {}
