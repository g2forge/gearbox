package com.g2forge.gearbox.issue;

import org.junit.Test;

import com.g2forge.alexandria.test.HAssert;

public class TestIIssueType {
	@Test
	public void generic() {
		HAssert.assertEquals(ExampleIssueType.class.getName() + ".Generic", ExampleIssueType.Generic.getCode());
	}

	@Test
	public void override() {
		HAssert.assertEquals(ExampleIssueType.class.getName() + ".Override", ExampleIssueType.Override.getCode());
	}
}
