package com.g2forge.gearbox.command.converter;

import java.util.stream.Stream;

import com.g2forge.alexandria.annotations.note.Note;
import com.g2forge.alexandria.annotations.note.NoteType;
import com.g2forge.alexandria.java.core.error.RuntimeReflectionException;
import com.g2forge.alexandria.java.core.helpers.HStream;
import com.g2forge.habitat.metadata.type.predicate.IPredicateType;
import com.g2forge.habitat.metadata.value.predicate.ConstantPredicate;
import com.g2forge.habitat.metadata.value.predicate.IPredicate;
import com.g2forge.habitat.metadata.value.subject.ISubject;

public interface ICommandConverter__ {
	@Note(type = NoteType.TODO, value = "Check for metadata on the type containing the method, will require upgrades to metadata first", issue = "G2-469")
	public static <C extends ICommandConverter__> IPredicate<C> bind(ISubject subject, IPredicateType<C> predicateType, final Class<C> klass) {
		final CommandConverters commandConverters = subject.get(CommandConverters.class);
		if (commandConverters == null) return ConstantPredicate.absent(subject, predicateType);
		final Class<? extends ICommandConverter__> rendererType = HStream.findOne(Stream.of(commandConverters.value()).map(CommandConverter::value).filter(klass::isAssignableFrom));
		try {
			final C thingy = klass.cast(rendererType.newInstance());
			return ConstantPredicate.present(subject, predicateType, thingy);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeReflectionException(e);
		}
	}
}
