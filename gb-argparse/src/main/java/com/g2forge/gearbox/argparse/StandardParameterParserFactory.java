package com.g2forge.gearbox.argparse;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.alexandria.java.fluent.optional.IOptional;
import com.g2forge.alexandria.java.fluent.optional.NullableOptional;

public class StandardParameterParserFactory implements IParameterParserFactory, ISingleton {
	protected StandardParameterParserFactory() {}

	protected static final StandardParameterParserFactory INSTANCE = new StandardParameterParserFactory();

	public static StandardParameterParserFactory create() {
		return INSTANCE;
	}

	@Override
	public IParameterParser apply(IParameterInfo parameter) {
		final Class<?> type = parameter.getType();
		return computeParser(type);
	}

	private IParameterParser computeParser(final Class<?> type) {
		if (Path.class.equals(type)) return BasicParameterParser.PATH;
		if (String.class.equals(type)) return BasicParameterParser.STRING;
		if (Boolean.TYPE.equals(type) || Boolean.class.equals(type)) return BasicParameterParser.BOOLEAN;
		if (type.isArray()) {
			final Class<?> componentType = type.getComponentType();
			final IParameterParser componentParser = computeParser(componentType);
			return new IParameterParser() {
				@Override
				public IOptional<Object> getDefault(IParameterInfo parameter) {
					if (parameter.getSubject().bind(NamedParameter.class).isPresent()) return NullableOptional.of(null);
					return NullableOptional.empty();
				}

				@Override
				public Object parse(IParameterInfo parameter, ListIterator<String> argumentIterator) {
					final List<String> arguments = HCollection.asList(argumentIterator.next().split(",+"));
					final IParameterInfo componentParameter = new IParameterInfo.ParameterInfo(parameter).toBuilder().type(componentType).build();
					final List<Object> values = new ArrayList<>();
					for (final ListIterator<String> iterator = arguments.listIterator(); iterator.hasNext();) {
						values.add(componentParser.parse(componentParameter, iterator));
					}
					return values.toArray(size -> (Object[]) Array.newInstance(componentType, size));
				}
			};
		}
		return null;
	}
}
