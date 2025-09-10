package com.g2forge.gearbox.issue;

import com.g2forge.alexandria.java.function.ISupplier;

public interface IEnumIssueType<Type extends IEnumIssueType<Type, Payload>, Payload> extends IIssueType<Payload> {
	@Override
	public default IIssue<Type, Payload> of(ISupplier<? extends Payload> supplier) {
		@SuppressWarnings("unchecked")
		final Type _this = (Type) this;
		return new LateIssue<>(_this, supplier);
	}

	@Override
	public default IIssue<Type, Payload> of(Payload payload) {
		@SuppressWarnings("unchecked")
		final Type _this = (Type) this;
		return new Issue<>(_this, payload);
	}
}
