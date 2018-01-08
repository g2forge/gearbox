package com.g2forge.gearbox.functional.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.gearbox.functional.Flag;
import com.g2forge.gearbox.functional.runner.IProcess;
import com.g2forge.gearbox.functional.runner.IRunner;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
class ProxyInvocationHandler implements InvocationHandler {
	@Getter(AccessLevel.PROTECTED)
	protected final IRunner runner;

	protected <T> void add(List<String> command, IArgument<T> argument) {
		if (argument.getType().isArray()) {
			if (String.class.equals(argument.getType().getComponentType())) {
				for (String value : (String[]) argument.get()) {
					command.add(value);
				}
				return;
			}
		} else if (Boolean.TYPE.equals(argument.getType())) {
			final Flag flag = argument.getAnnotation(Flag.class);
			if (flag != null) {
				if ((Boolean) argument.get()) command.add(flag.value());
				return;
			}
		}
		throw new IllegalArgumentException();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final Function<IProcess, Object> result;
		if (String.class.equals(method.getReturnType())) {
			result = process -> {
				final StringBuilder retVal = new StringBuilder();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getStandardOut()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						retVal.append(line).append("\n");
					}
				} catch (IOException exception) {
					throw new RuntimeIOException(exception);
				}

				return retVal.toString();
			};
		} else throw new IllegalArgumentException();

		final Parameter[] parameters = method.getParameters();

		final List<String> command = new ArrayList<>();
		command.add(method.getName());
		for (int i = 0; i < method.getParameterCount(); i++) {
			add(command, new Argument(args[i], parameters[i]));
		}

		return result.apply(getRunner().run(command));
	}
}