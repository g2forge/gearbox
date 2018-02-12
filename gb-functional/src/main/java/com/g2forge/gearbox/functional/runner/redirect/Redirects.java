package com.g2forge.gearbox.functional.runner.redirect;

import com.g2forge.gearbox.functional.runner.IStandardIO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Redirects implements IStandardIO<IRedirect, IRedirect> {
	protected final IRedirect standardInput;

	protected final IRedirect standardOutput;

	protected final IRedirect standardError;
}