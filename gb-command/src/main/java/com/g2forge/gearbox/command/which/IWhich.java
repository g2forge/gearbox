package com.g2forge.gearbox.command.which;

import com.g2forge.gearbox.command.converter.dumb.Command;

public interface IWhich extends IWhichLike {
	@Command({ "which" })
	public boolean isInstalled(String executable);

	@Command({ "which" })
	public String which(String executable);
}
