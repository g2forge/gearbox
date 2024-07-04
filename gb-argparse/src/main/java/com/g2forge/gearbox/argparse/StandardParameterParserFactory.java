package com.g2forge.gearbox.argparse;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.alexandria.java.fluent.optional.IOptional;
import com.g2forge.alexandria.java.fluent.optional.NullableOptional;
import com.g2forge.habitat.metadata.value.predicate.IPredicate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class StandardParameterParserFactory implements IParameterParserFactory, ISingleton {
	@Getter(AccessLevel.PROTECTED)
	@RequiredArgsConstructor
	protected static class ArrayParameterParser implements IParameterParser {
		protected final Class<?> componentType;

		protected final IParameterParser componentParser;

		@Override
		public IOptional<Object> getDefault(IParameterInfo parameter) {
			if (parameter.getSubject().bind(Parameter.class).isPresent()) return NullableOptional.of(null);
			return NullableOptional.empty();
		}

		@Override
		public Object parse(IParameterInfo parameter, ListIterator<String> argumentIterator) {
			final List<String> arguments = HCollection.asList(argumentIterator.next().split(",+"));
			final IParameterInfo componentParameter = new IParameterInfo.ParameterInfo(parameter).toBuilder().type(getComponentType()).build();
			final List<Object> values = new ArrayList<>();
			for (final ListIterator<String> iterator = arguments.listIterator(); iterator.hasNext();) {
				values.add(getComponentParser().parse(componentParameter, iterator));
			}
			return values.toArray(size -> (Object[]) Array.newInstance(componentType, size));
		}
	}

	protected static final StandardParameterParserFactory INSTANCE = new StandardParameterParserFactory();

	public static StandardParameterParserFactory create() {
		return INSTANCE;
	}

	protected StandardParameterParserFactory() {}

	@Override
	public IParameterParser apply(IParameterInfo parameter) {
		final Class<?> type = parameter.getType();
		final IPredicate<Parameter> annotation = parameter.getSubject().bind(Parameter.class);
		if (annotation.isPresent()) {
			final Class<? extends IParameterParser> parserType = annotation.get0().parser();
			if (parserType != Parameter.DefaultParser.class) {
				try {
					final Constructor<? extends IParameterParser> constructor = parserType.getConstructor();
					return constructor.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return computeParser(type);
	}

	private IParameterParser computeParser(final Class<?> type) {
		if (Path.class.equals(type)) return BasicParameterParser.PATH;
		if (String.class.equals(type)) return BasicParameterParser.STRING;
		if (Boolean.TYPE.equals(type) || Boolean.class.equals(type)) return BasicParameterParser.BOOLEAN;
		if (Enum.class.isAssignableFrom(type)) return BasicParameterParser.ENUM;
		if (type.isArray()) {
			final Class<?> componentType = type.getComponentType();
			final IParameterParser componentParser = computeParser(componentType);
			return new ArrayParameterParser(componentType, componentParser);
		}
		return null;
	}
}
