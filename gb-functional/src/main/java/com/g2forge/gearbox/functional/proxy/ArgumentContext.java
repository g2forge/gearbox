package com.g2forge.gearbox.functional.proxy;

import com.g2forge.gearbox.functional.control.IArgument;
import com.g2forge.gearbox.functional.control.IArgumentContext;
import com.g2forge.gearbox.functional.control.ICommandBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
class ArgumentContext implements IArgumentContext {
	protected final ICommandBuilder command;

	protected final IArgument<Object> argument;
}