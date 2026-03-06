package com.g2forge.gearbox.command.process;

import com.g2forge.alexandria.command.invocation.ICommandArgumentType;
import com.g2forge.alexandria.java.core.marker.ISingleton;

public class MetaCommandArgumentType implements ICommandArgumentType<MetaCommandArgument>, ISingleton {
	protected static final MetaCommandArgumentType INSTANCE = new MetaCommandArgumentType();

	protected static MetaCommandArgumentType create() {
		return INSTANCE;
	}

	@Override
	public MetaCommandArgument create(String string) {
		return new MetaCommandArgument(string, null);
	}

	@Override
	public MetaCommandArgument create(String string, MetaCommandArgument original) {
		return original.toBuilder().value(string).build();
	}

	@Override
	public String get(MetaCommandArgument argument) {
		return argument.getValue();
	}
}
