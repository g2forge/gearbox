package com.g2forge.gearbox.argparse;

import java.nio.file.Paths;
import java.util.ListIterator;

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
		public Object parse(IParameterInfo parameter, ListIterator<String> argumentIterator) {
			if (parameter.getSubject().bind(Parameter.class).isPresent()) return true;
			else return Boolean.valueOf(argumentIterator.next());
		}
	},
	PATH {
		@Override
		public IOptional<Object> getDefault(IParameterInfo parameter) {
			if (parameter.getSubject().bind(Parameter.class).isPresent()) return NullableOptional.of(null);
			return NullableOptional.empty();
		}

		@Override
		public Object parse(IParameterInfo parameter, ListIterator<String> argumentIterator) {
			return Paths.get(argumentIterator.next());
		}
	},
	STRING {
		@Override
		public IOptional<Object> getDefault(IParameterInfo parameter) {
			if (parameter.getSubject().bind(Parameter.class).isPresent()) return NullableOptional.of(null);
			return NullableOptional.empty();
		}

		@Override
		public Object parse(IParameterInfo parameter, ListIterator<String> argumentIterator) {
			return argumentIterator.next();
		}
	};
}