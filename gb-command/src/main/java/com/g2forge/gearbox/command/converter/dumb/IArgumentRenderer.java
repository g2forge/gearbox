package com.g2forge.gearbox.command.converter.dumb;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.g2forge.alexandria.java.core.error.RuntimeReflectionException;
import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.habitat.metadata.access.ITypedMetadataAccessor;
import com.g2forge.habitat.metadata.access.indirect.IndirectMetadata;
import com.g2forge.habitat.metadata.type.predicate.IPredicateType;
import com.g2forge.habitat.metadata.value.predicate.ConstantPredicate;
import com.g2forge.habitat.metadata.value.predicate.IPredicate;
import com.g2forge.habitat.metadata.value.subject.ISubject;

@IndirectMetadata(IArgumentRenderer.MetadataAccessor.class)
public interface IArgumentRenderer<T> {
	public static class MetadataAccessor implements ITypedMetadataAccessor<IArgumentRenderer<?>, ISubject, IPredicateType<IArgumentRenderer<?>>> {
		@Override
		public IPredicate<IArgumentRenderer<?>> bindTyped(ISubject subject, IPredicateType<IArgumentRenderer<?>> predicateType) {
			final ArgumentRenderer argumentRendererMetadata = subject.get(ArgumentRenderer.class);
			if (argumentRendererMetadata == null) return ConstantPredicate.absent(subject, predicateType);
			try {
				return ConstantPredicate.present(subject, predicateType, argumentRendererMetadata.value().getDeclaredConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeReflectionException(e);
			}
		}
	}

	public List<String> render(IMethodArgument<T> argument);
}
