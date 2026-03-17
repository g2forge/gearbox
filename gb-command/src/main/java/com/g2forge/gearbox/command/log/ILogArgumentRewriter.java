package com.g2forge.gearbox.command.log;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.g2forge.alexandria.java.core.error.RuntimeReflectionException;
import com.g2forge.habitat.metadata.access.ITypedMetadataAccessor;
import com.g2forge.habitat.metadata.access.indirect.IndirectMetadata;
import com.g2forge.habitat.metadata.type.predicate.IPredicateType;
import com.g2forge.habitat.metadata.value.predicate.ConstantPredicate;
import com.g2forge.habitat.metadata.value.predicate.IPredicate;
import com.g2forge.habitat.metadata.value.subject.ISubject;

@FunctionalInterface
@IndirectMetadata(ILogArgumentRewriter.MetadataAccessor.class)
public interface ILogArgumentRewriter {
	public static class MetadataAccessor implements ITypedMetadataAccessor<ILogArgumentRewriter, ISubject, IPredicateType<ILogArgumentRewriter>> {
		@Override
		public IPredicate<ILogArgumentRewriter> bindTyped(ISubject subject, IPredicateType<ILogArgumentRewriter> predicateType) {
			final LogArgumentRewriter logArgumentRewriterMetadata = subject.get(LogArgumentRewriter.class);
			if (logArgumentRewriterMetadata == null) {
				final SkipLog skipMetadata = subject.get(SkipLog.class);
				if (skipMetadata != null) return ConstantPredicate.present(subject, predicateType, SkipLogArgumentRewriter.create());

				final PasswordLog passwordMetadata = subject.get(PasswordLog.class);
				if (passwordMetadata != null) return ConstantPredicate.present(subject, predicateType, new PasswordLogArgumentRewriter(passwordMetadata.value()));
				return ConstantPredicate.absent(subject, predicateType);
			}
			try {
				return ConstantPredicate.present(subject, predicateType, logArgumentRewriterMetadata.value().getDeclaredConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeReflectionException(e);
			}
		}
	}

	public String rewrite(String argument, Map<String, Object> context);
}
