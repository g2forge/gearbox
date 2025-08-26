package com.g2forge.gearbox.ssh.agent;

import java.util.regex.Pattern;

import com.g2forge.alexandria.java.close.AGuaranteeClose;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public abstract class ASSHAgent extends AGuaranteeClose implements ISSHAgent {
	protected static final Pattern PATTERN_SSH_ENV_VAR_LINE = Pattern.compile("(?<key>SSH_[A-Z_]+)=(?<value>[^;]+);.*");

	protected final String socket;

	protected final String pid;
}