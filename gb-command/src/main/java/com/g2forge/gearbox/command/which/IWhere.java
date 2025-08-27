package com.g2forge.gearbox.command.which;

import com.g2forge.gearbox.command.converter.dumb.Command;

public interface IWhere extends IWhichLike {
	@Command({ "where.exe" })
	public boolean isInstalled(String executable);

	@Command({ "where.exe" })
	public String which(String executable);
}
