package com.g2forge.gearbox.command.converter;

import com.g2forge.gearbox.command.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;
import com.g2forge.habitat.metadata.access.ITypedMetadataAccessor;
import com.g2forge.habitat.metadata.access.indirect.IndirectMetadata;
import com.g2forge.habitat.metadata.type.predicate.IPredicateType;
import com.g2forge.habitat.metadata.value.predicate.IPredicate;
import com.g2forge.habitat.metadata.value.subject.ISubject;

@FunctionalInterface
@IndirectMetadata(ICommandConverterR_.MetadataAccessor.class)
public interface ICommandConverterR_ extends ICommandConverter__ {
	public static class MetadataAccessor implements ITypedMetadataAccessor<ICommandConverterR_, ISubject, IPredicateType<ICommandConverterR_>> {
		@Override
		public IPredicate<ICommandConverterR_> bindTyped(ISubject subject, IPredicateType<ICommandConverterR_> predicateType) {
			return ICommandConverter__.bind(subject, predicateType, ICommandConverterR_.class);
		}
	}

	public <T> ProcessInvocation<T> apply(ProcessInvocation<T> processInvocation, MethodInvocation methodInvocation);
}
