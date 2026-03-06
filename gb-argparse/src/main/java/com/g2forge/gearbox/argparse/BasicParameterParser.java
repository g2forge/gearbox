package com.g2forge.gearbox.argparse;

import java.util.ListIterator;

import com.g2forge.alexandria.command.invocation.CommandArgument;
import com.g2forge.alexandria.java.core.enums.HEnum;
import com.g2forge.alexandria.java.fluent.optional.IOptional;
import com.g2forge.alexandria.java.fluent.optional.NullableOptional;

public enum BasicParameterParser implements IParameterParser {
	BOOLEAN {
		@Override
		public IOptional<Object> getDefault(IParameterInfo parameter) {
			if (parameter.getSubject().bind(Parameter.class).isPresent()) return NullableOptional.of(false);
			return NullableOptional.empty();
		}

		@Override
		public <A> Object parse(IParameterInfo parameter, ListIterator<CommandArgument<A>> argumentIterator) {
			if (parameter.getSubject().bind(Parameter.class).isPresent()) return true;
			else return Boolean.valueOf(argumentIterator.next().getString());
		}
	},
	PATH {
		@Override
		public IOptional<Object> getDefault(IParameterInfo parameter) {
			if (parameter.getSubject().bind(Parameter.class).isPresent()) return NullableOptional.of(null);
			return NullableOptional.empty();
		}

		@Override
		public <A> Object parse(IParameterInfo parameter, ListIterator<CommandArgument<A>> argumentIterator) {
			return argumentIterator.next().getPath();
		}
	},
	STRING {
		@Override
		public IOptional<Object> getDefault(IParameterInfo parameter) {
			if (parameter.getSubject().bind(Parameter.class).isPresent()) return NullableOptional.of(null);
			return NullableOptional.empty();
		}

		@Override
		public <A> Object parse(IParameterInfo parameter, ListIterator<CommandArgument<A>> argumentIterator) {
			return argumentIterator.next().getString();
		}
	},
	ENUM {
		@Override
		public IOptional<Object> getDefault(IParameterInfo parameter) {
			if (parameter.getSubject().bind(Parameter.class).isPresent()) return NullableOptional.of(null);
			return NullableOptional.empty();
		}

		@SuppressWarnings("unchecked")
		@Override
		public <A> Object parse(IParameterInfo parameter, ListIterator<CommandArgument<A>> argumentIterator) {
			@SuppressWarnings("rawtypes")
			final Class<? extends Enum> cast = (Class<? extends Enum>) parameter.getType();
			return HEnum.valueOfInsensitive(cast, argumentIterator.next().getString());
		}
	};
}