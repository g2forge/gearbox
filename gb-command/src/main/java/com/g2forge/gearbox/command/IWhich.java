package com.g2forge.gearbox.command;

import com.g2forge.gearbox.command.converter.dumb.Command;
import com.g2forge.gearbox.command.proxy.method.ICommandInterface;

public interface IWhich extends ICommandInterface {
	@Command({ "which" })
	public boolean isInstalled(String executable);

	@Command({ "which" })
	public String which(String executable);
}
