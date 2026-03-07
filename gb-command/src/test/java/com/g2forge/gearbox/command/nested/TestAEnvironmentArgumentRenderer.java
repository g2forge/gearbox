package com.g2forge.gearbox.command.nested;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.command.invocation.environment.IEnvironment;
import com.g2forge.alexandria.command.invocation.environment.MapEnvironment;
import com.g2forge.alexandria.command.invocation.environment.SystemEnvironment;
import com.g2forge.alexandria.command.invocation.environment.modified.EnvironmentModifier;
import com.g2forge.alexandria.command.invocation.environment.modified.EnvironmentValue;
import com.g2forge.alexandria.command.invocation.environment.modified.ModifiedEnvironment;
import com.g2forge.alexandria.java.core.error.UnreachableCodeError;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.helpers.HMap;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.gearbox.command.process.MetaCommandArgument;
import com.g2forge.gearbox.command.process.MetaCommandArgumentType;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

public class TestAEnvironmentArgumentRenderer {
	protected static class EnvironmentArgumentRenderer extends AEnvironmentArgumentRenderer {}

	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	protected static class TestMethodArgument<T> implements IMethodArgument<T> {
		protected final String name;

		protected final T value;

		@Override
		public T get() {
			return getValue();
		}

		@Override
		public Type getGenericType() {
			throw new UnreachableCodeError();
		}

		@Override
		public ISubject getMetadata() {
			return null;
		}

		@Override
		public Class<T> getType() {
			throw new UnreachableCodeError();
		}
	}

	@Test
	public void insertInheritEnvironment() {
		final IEnvironment base = MapEnvironment.builder().variable("key", "value").build();
		final IEnvironment environment = ModifiedEnvironment.builder().base(base).modifier("key", EnvironmentModifier.Inherit).build();
		HAssert.assertEquals(HCollection.asList("outer-arg0", "--env", "key=value", "outer-arg1", "inner-arg0"), testInsertEnvironment(environment));
	}

	@Test
	public void insertInjectEnvironment() {
		final IEnvironment base = MapEnvironment.builder().variable("key0", "value0").build();
		final IEnvironment environment = ModifiedEnvironment.builder().base(base).modifier("key1", new EnvironmentValue("value1")).build();
		HAssert.assertEquals(HCollection.asList("outer-arg0", "--env", "key0=value0", "--env", "key1=value1", "outer-arg1", "inner-arg0"), testInsertEnvironment(environment));
	}

	@Test
	public void insertMapEnvironment() {
		HAssert.assertEquals(HCollection.asList("outer-arg0", "--env", "key=value", "outer-arg1", "inner-arg0"), testInsertEnvironment(MapEnvironment.builder().variable("key", "value").build()));
	}

	@Test
	public void insertNoEnvironment() {
		HAssert.assertEquals(HCollection.asList("outer-arg0", "outer-arg1", "inner-arg0"), testInsertEnvironment(null));
	}

	@Test
	public void insertSystemEnvironment() {
		HAssert.assertEquals(HCollection.asList("outer-arg0", "outer-arg1", "inner-arg0"), testInsertEnvironment(SystemEnvironment.create()));
	}

	@Test
	public void insertUnspecifiedEnvironment() {
		final IEnvironment base = MapEnvironment.builder().variable("key", "value").build();
		final IEnvironment environment = ModifiedEnvironment.builder().base(base).modifier("key", EnvironmentModifier.Unspecified).build();
		HAssert.assertEquals(HCollection.asList("outer-arg0", "outer-arg1", "inner-arg0"), testInsertEnvironment(environment));
	}

	@Test
	public void renderEmptyEnvironment() {
		final List<MetaCommandArgument> rendered = new EnvironmentArgumentRenderer().render(new TestMethodArgument<>("arg0", HMap.empty()));
		HAssert.assertEquals(HCollection.emptyList(), rendered.stream().map(MetaCommandArgument::getValue).toList());
	}

	@Test
	public void renderNullEnvironment() {
		final List<MetaCommandArgument> rendered = new EnvironmentArgumentRenderer().render(new TestMethodArgument<>("arg0", null));
		HAssert.assertEquals(HCollection.emptyList(), rendered.stream().map(MetaCommandArgument::getValue).toList());
	}

	protected List<String> testInsertEnvironment(IEnvironment environment) {
		final CommandInvocation<MetaCommandArgument, IRedirect, IRedirect> inner = CommandInvocation.<MetaCommandArgument, IRedirect, IRedirect>builder().type(MetaCommandArgumentType.create()).argument(new MetaCommandArgument("inner-arg0", null)).environment(environment).build();
		final CommandInvocation.CommandInvocationBuilder<MetaCommandArgument, IRedirect, IRedirect> outer = CommandInvocation.<MetaCommandArgument, IRedirect, IRedirect>builder().type(MetaCommandArgumentType.create()).argument(new MetaCommandArgument("outer-arg0", null)).argument(new MetaCommandArgument("outer-arg1", null));
		new EnvironmentArgumentRenderer().insertEnvironmentArguments().accept(inner, outer);
		final List<String> actual = MetaCommandArgument.toStrings(outer.build().getArguments());
		return actual;
	}
}
