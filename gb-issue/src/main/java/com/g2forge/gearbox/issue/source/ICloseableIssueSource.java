package com.g2forge.gearbox.issue.source;

import com.g2forge.alexandria.java.close.ICloseable;
import com.g2forge.gearbox.issue.IIssueSource;
import com.g2forge.gearbox.issue.IIssueType;

public interface ICloseableIssueSource<Type extends IIssueType<?>> extends IIssueSource<Type>, ICloseable {}
