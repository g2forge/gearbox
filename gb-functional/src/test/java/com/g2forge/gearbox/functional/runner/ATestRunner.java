package com.g2forge.gearbox.functional.runner;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.gearbox.functional.IUtils;
import com.g2forge.gearbox.functional.proxy.Proxifier;

import lombok.Getter;

public abstract class ATestRunner {
	protected final Proxifier proxifier = new Proxifier();

	@Getter(lazy = true)
	private final IRunner runner = createRunner();

	@Getter(lazy = true)
	private final IUtils utils = proxifier.generate(getRunner(), IUtils.class);

	protected abstract IRunner createRunner();

	@Test
	public void exitcode() {
		Assert.assertFalse(getUtils().false_());
		Assert.assertTrue(getUtils().true_());
	}
}
