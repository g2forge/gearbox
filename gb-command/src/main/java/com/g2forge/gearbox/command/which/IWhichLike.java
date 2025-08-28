package com.g2forge.gearbox.command.which;

import com.g2forge.alexandria.java.core.enums.EnumException;
import com.g2forge.alexandria.java.platform.HPlatform;
import com.g2forge.alexandria.java.platform.PlatformCategory;
import com.g2forge.gearbox.command.proxy.CommandFactory;
import com.g2forge.gearbox.command.proxy.ICommandFactory;
import com.g2forge.gearbox.command.proxy.ICommandProxyFactory;
import com.g2forge.gearbox.command.proxy.method.ICommandInterface;

@CommandFactory(IWhichLike.IWhichLikeCommandFactory.class)
public interface IWhichLike extends ICommandInterface {
	public static class IWhichLikeCommandFactory implements ICommandFactory<IWhichLike> {
		@Override
		public IWhichLike create(ICommandProxyFactory factory) {
			final PlatformCategory category = HPlatform.getPlatform().getCategory();
			switch (category) {
				case Microsoft:
					return factory.apply(IWhere.class);
				case Posix:
					return factory.apply(IWhich.class);
				default:
					throw new EnumException(PlatformCategory.class, category);
			}
		}
	}

	public boolean isInstalled(String executable);

	public String which(String executable);
}
