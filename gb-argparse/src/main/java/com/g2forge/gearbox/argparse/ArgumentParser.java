package com.g2forge.gearbox.argparse;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.helpers.HStream;
import com.g2forge.alexandria.java.fluent.optional.IOptional;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.text.HString;
import com.g2forge.habitat.metadata.Metadata;
import com.g2forge.habitat.metadata.value.predicate.IPredicate;
import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ArgumentParser<T> {
	protected static class ArgumentsParser implements IArgumentsParser {
		protected final List<? extends IParameterInfo> parameters;

		/** An in-order list of the positional parameters. May have different size than the orignal parameters as some may be named. */
		protected final List<ParameterParserInfo> positional;

		/** A map from their names to the named parameters. May have different size than the original parameters as some may be positional. */
		protected final Map<String, ParameterParserInfo> named;

		/** A list of parsers, one for each input parameter. */
		protected final List<IParameterParser> parsers;

		@Getter(AccessLevel.PROTECTED)
		protected final boolean foundAnnotations;

		public ArgumentsParser(final IParameterParserFactory parameterParserFactory, final List<? extends IParameterInfo> parameters) {
			// Parse the parameter model from the constructor
			this.parameters = parameters;
			positional = new ArrayList<>();
			named = new HashMap<>();
			parsers = new ArrayList<>();
			boolean foundAnnotations = false;
			for (int i = 0; i < parameters.size(); i++) {
				final IParameterInfo parameter = parameters.get(i);

				final IParameterParser parameterTypeParser = parameterParserFactory.apply(parameter);
				parsers.add(parameterTypeParser);

				final ParameterParserInfo info = new ParameterParserInfo(i, parameterTypeParser);
				final ISubject subject = parameter.getSubject();
				final Parameter annotation = subject.get(Parameter.class);
				if (annotation != null) {
					named.put(annotation.value(), info);
					foundAnnotations = true;
				} else positional.add(info);
			}
			this.foundAnnotations = foundAnnotations;
		}

		public ArgumentsParser(final List<? extends IParameterInfo> parameters) {
			this(StandardParameterParserFactory.create(), parameters);
		}

		public Object[] apply(List<String> arguments) {
			final Object[] parsed = new Object[parameters.size()];
			final boolean[] set = new boolean[parameters.size()];
			int p = 0;
			// Parse the arguments
			for (final ListIterator<String> argumentIterator = arguments.listIterator(); argumentIterator.hasNext();) {
				final int argumentIndex = argumentIterator.nextIndex();
				final String argument = argumentIterator.next();
				try {
					boolean foundNamed = false;
					for (Map.Entry<String, ParameterParserInfo> entry : named.entrySet()) {
						if (argument.startsWith(entry.getKey())) {
							final ParameterParserInfo info = entry.getValue();
							final int parameterIndex = info.getIndex();
							parsed[parameterIndex] = info.getParser().parse(parameters.get(parameterIndex), argumentIterator);
							set[parameterIndex] = true;
							foundNamed = true;
							break;
						}
					}
					if (!foundNamed) {
						argumentIterator.previous();
						final ParameterParserInfo info = positional.get(p++);
						final int index = info.getIndex();
						parsed[index] = info.getParser().parse(parameters.get(index), argumentIterator);
						set[index] = true;
					}
				} catch (Throwable throwable) {
					throw new UnparseableArgumentException(argumentIndex, argument, throwable);
				}
			}

			// Fill in any unparsed parameters with defaults
			for (IParameterInfo parameter : parameters) {
				if (!set[parameter.getIndex()]) {
					final IOptional<Object> defaultValue = parsers.get(parameter.getIndex()).getDefault(parameter);
					if (defaultValue.isEmpty()) throw new UnspecifiedParameterException(parameter);
					else {
						parsed[parameter.getIndex()] = defaultValue.get();
						set[parameter.getIndex()] = true;
					}
				}
			}
			return parsed;
		}

		@Override
		public String generateHelp() {
			final StringBuilder retVal = new StringBuilder();
			final Map<String, String> positionalHelp = new LinkedHashMap<>();
			for (ParameterParserInfo info : positional) {
				final IParameterInfo parameter = parameters.get(info.getIndex());
				if (!retVal.isEmpty()) retVal.append(' ');
				retVal.append('<').append(parameter.getName()).append('>');

				final IPredicate<ArgumentHelp> predicate = parameter.getSubject().bind(ArgumentHelp.class);
				if (predicate.isPresent()) positionalHelp.put(parameter.getName(), predicate.get0().value());
			}
			final boolean hasNamed = named.isEmpty();
			if (!hasNamed && !retVal.isEmpty()) retVal.append(" [...]");

			if (!positionalHelp.isEmpty() || !hasNamed) {
				if (!retVal.isEmpty()) retVal.append("\n");
				final int padded = HStream.concat(positionalHelp.keySet().stream(), named.keySet().stream()).mapToInt(String::length).max().getAsInt();

				if (!positionalHelp.isEmpty()) {
					for (Map.Entry<String, String> entry : positionalHelp.entrySet()) {
						if (!retVal.isEmpty()) retVal.append('\n');
						retVal.append(entry.getKey()).append(' ').append(entry.getValue());
					}
				}

				if (!hasNamed) {
					for (Map.Entry<String, ParameterParserInfo> entry : named.entrySet()) {
						if (!retVal.isEmpty()) retVal.append('\n');
						final IParameterInfo parameter = parameters.get(entry.getValue().getIndex());
						retVal.append(HString.pad(entry.getKey(), " ", padded));
						final ArgumentHelp argumentHelp = parameter.getSubject().get(ArgumentHelp.class);
						if (argumentHelp != null) retVal.append(' ').append(argumentHelp.value());
					}
				}
			}

			return retVal.toString();
		}
	}

	public enum HelpArguments {
		STANDARD {
			@Override
			public boolean isHelp(List<String> arguments) {
				if (arguments.size() == 1) return STANDARD_HELP_ARGUMENTS.contains(arguments.get(0));
				return false;
			}
		},
		EMPTY {
			@Override
			public boolean isHelp(List<String> arguments) {
				return arguments.isEmpty();
			}
		};

		public abstract boolean isHelp(List<String> arguments);
	}

	protected interface IArgumentsParser extends IFunction1<List<String>, Object[]> {
		public String generateHelp();
	}

	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	protected static class ParameterParserInfo {
		protected final int index;

		protected final IParameterParser parser;
	}

	protected static final Set<String> STANDARD_HELP_ARGUMENTS = HCollection.asSet("/h", "/?", "-h", "-help", "--help");

	public static <T> T parse(Class<T> type, List<String> arguments) {
		return new ArgumentParser<>(type).parse(arguments);
	}

	protected final Class<T> type;

	protected final Set<HelpArguments> help;

	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private final Constructor<T> constructor = findConstructor();

	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private final IArgumentsParser argumentsParser = computeArgumentsParser();

	public ArgumentParser(Class<T> type) {
		this(type, EnumSet.of(HelpArguments.STANDARD));
	}

	protected ArgumentsParser computeArgumentsParser() {
		final java.lang.reflect.Parameter[] parameterActuals = getConstructor().getParameters();
		final List<IParameterInfo> parameterInfos = new ArrayList<>();
		for (int i = 0; i < parameterActuals.length; i++) {
			parameterInfos.add(new IParameterInfo.ParameterInfoAdapter(i, parameterActuals[i]));
		}
		final ArgumentsParser argumentsParser = new ArgumentsParser(parameterInfos);
		if (!argumentsParser.isFoundAnnotations()) {
			boolean foundAnnotationsOnFields = false;
			Class<?> type = getType();
			outer: while (!Object.class.equals(type)) {
				for (Field field : getType().getDeclaredFields()) {
					if (Metadata.getStandard().of(field, null).bind(Parameter.class).isPresent()) {
						foundAnnotationsOnFields = true;
						break outer;
					}
				}
				type = type.getSuperclass();
			}
			if (foundAnnotationsOnFields) throw new RuntimeException(String.format("There are argument parsing exceptions on the fields of %1$s, but not the parameters of %2$s.  Please ensure you have \"lombok.anyConstructor.addConstructorProperties = true\", \"lombok.copyableAnnotations += %3$s\", and \"lombok.copyableAnnotations += %4$s\" in the relevant lombok.config or manually copy the annotations to your constructor parameters!", getType(), getConstructor(), Parameter.class, ArgumentHelp.class));
		}
		return argumentsParser;
	}

	private T create(final Object[] parsed) {
		final Constructor<T> constructor = getConstructor();
		try {
			return constructor.newInstance(parsed);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private Constructor<T> findConstructor() {
		final Constructor<?>[] constructors = type.getDeclaredConstructors();
		if (constructors.length != 1) throw new IllegalArgumentException(String.format("Argument type %1$s has %2$d constructors, only single constructor types are supported (for now).", type, constructors.length));

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Constructor<T> constructor = (Constructor) constructors[0];
		return constructor;
	}

	public T parse(List<String> arguments) {
		final IArgumentsParser argumentsParser = getArgumentsParser();

		final boolean help = getHelp().stream().filter(helpArguments -> helpArguments.isHelp(arguments)).findAny().isPresent();
		if (help) throw new ArgumentHelpException(argumentsParser.generateHelp());

		final Object[] parsed = argumentsParser.apply(arguments);
		return create(parsed);
	}
}
