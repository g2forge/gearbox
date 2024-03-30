package com.g2forge.gearbox.argparse;

import java.lang.reflect.Parameter;

import com.g2forge.alexandria.java.adt.name.IStringNamed;
import com.g2forge.habitat.metadata.Metadata;
import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

public interface IParameterInfo extends IStringNamed {
	public int getIndex();

	public ISubject getSubject();

	public Class<?> getType();

	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	public static class ParameterInfo implements IParameterInfo {
		protected final int index;

		protected final Class<?> type;

		protected final String name;

		protected final ISubject subject;

		public ParameterInfo(IParameterInfo that) {
			this(that.getIndex(), that.getType(), that.getName(), that.getSubject());
		}
	}

	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	public static class ParameterInfoAdapter implements IParameterInfo {
		protected final int index;

		protected final Parameter parameter;

		@Override
		public ISubject getSubject() {
			return Metadata.getStandard().of(parameter, null);
		}

		@Override
		public Class<?> getType() {
			return getParameter().getType();
		}

		@Override
		public String getName() {
			return getParameter().getName();
		}
	}
}
