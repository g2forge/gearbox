package com.g2forge.gearbox.issue;

import com.g2forge.alexandria.java.function.ISupplier;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface IIssueType<Payload> {
	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	public static class LateIssue<Type extends IIssueType<Payload>, Payload> implements IIssue<Type, Payload> {
		protected final Type type;

		private final ISupplier<? extends Payload> supplier;

		@Getter(lazy = true, value = AccessLevel.PUBLIC)
		private final Payload payload = supplier.get();
	}

	public default String getCode() {
		@SuppressWarnings("rawtypes")
		final Class<? extends IIssueType> klass = getClass();
		if (this instanceof Enum) {
			final String name = ((Enum<?>) this).name();
			if (klass.isEnum()) return klass.getName() + "." + name;
			else return klass.getSuperclass().getName() + "." + name;
		}
		return klass.toString();
	}

	public String getDescription();

	public Level getLevel();

	public String computeMessage(Payload payload);

	public default IIssue<? extends IIssueType<Payload>, Payload> of(Payload payload) {
		return new Issue<>(this, payload);
	}

	public default IIssue<? extends IIssueType<Payload>, Payload> of(ISupplier<? extends Payload> supplier) {
		return new LateIssue<>(this, supplier);
	}
}
