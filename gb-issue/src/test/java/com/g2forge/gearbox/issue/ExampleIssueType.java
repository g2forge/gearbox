package com.g2forge.gearbox.issue;

import com.g2forge.alexandria.java.function.ISupplier;

public enum ExampleIssueType implements IIssueType<ExamplePayload> {
	Generic,
	Override {
		@Override
		public String getDescription() {
			return "This description is from a method override";
		}
	};

	@Override
	public String computeMessage(ExamplePayload payload) {
		return getDescription() + " at " + payload;
	}

	@Override
	public String getDescription() {
		return "There was an input error";
	}

	@Override
	public Level getLevel() {
		return Level.ERROR;
	}

	@Override
	public IIssue<ExampleIssueType, ExamplePayload> of(ISupplier<? extends ExamplePayload> supplier) {
		return new LateIssue<>(this, supplier);
	}

	@Override
	public IIssue<ExampleIssueType, ExamplePayload> of(ExamplePayload payload) {
		return new Issue<>(this, payload);
	}
}