package com.g2forge.gearbox.command.converter.dumb;

import com.g2forge.gearbox.command.converter.IMethodArgument;

import java.util.List;
import java.util.Objects;

public class UppercaseToStringArgumentRenderer implements IArgumentRenderer<Object> {
    @Override
    public List<String> render(IMethodArgument<Object> argument) {
        return HDumbCommandConverter.computeString(argument, Objects.toString(argument.get()).toUpperCase());
    }
}

