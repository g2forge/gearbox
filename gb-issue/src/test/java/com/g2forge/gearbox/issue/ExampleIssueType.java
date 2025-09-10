package com.g2forge.gearbox.issue;

public enum ExampleIssueType implements IEnumIssueType<ExampleIssueType, ExamplePayload> {
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
}