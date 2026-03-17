package com.g2forge.gearbox.command.log;

import java.util.List;
import java.util.Map;

import com.g2forge.alexandria.java.core.helpers.HCollection;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class ChainedLogArgumentRewriter implements ILogArgumentRewriter {
	protected final List<ILogArgumentRewriter> rewriters;

	public ChainedLogArgumentRewriter(ILogArgumentRewriter... rewriters) {
		this(HCollection.asList(rewriters));
	}

	@Override
	public String rewrite(String argument, Map<String, Object> context) {
		for (ILogArgumentRewriter rewriter : getRewriters()) {
			argument = rewriter.rewrite(argument, context);
		}
		return argument;
	}
}
