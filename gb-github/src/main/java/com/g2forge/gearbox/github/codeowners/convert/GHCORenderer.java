package com.g2forge.gearbox.github.codeowners.convert;

import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.type.function.TypeSwitch1;
import com.g2forge.enigma.backend.ITextAppender;
import com.g2forge.enigma.backend.convert.ARenderer;
import com.g2forge.enigma.backend.convert.IExplicitRenderable;
import com.g2forge.enigma.backend.convert.IRendering;
import com.g2forge.enigma.backend.convert.textual.ATextualRenderer;
import com.g2forge.enigma.backend.text.model.modifier.TextNestedModified;
import com.g2forge.gearbox.github.codeowners.GHCOBlank;
import com.g2forge.gearbox.github.codeowners.GHCOComment;
import com.g2forge.gearbox.github.codeowners.GHCOPattern;
import com.g2forge.gearbox.github.codeowners.GHCodeOwners;
import com.g2forge.gearbox.github.codeowners.IGHCOLine;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GHCORenderer extends ATextualRenderer<Object, IGHCORenderContext> {
	protected class GHCORenderContext extends ARenderContext implements IGHCORenderContext {

		public GHCORenderContext(TextNestedModified.TextNestedModifiedBuilder builder) {
			super(builder);
		}

		@Override
		protected IGHCORenderContext getThis() {
			return this;
		}
	}

	protected static class GHCORendering extends ARenderer.ARendering<Object, IGHCORenderContext, IExplicitRenderable<? super IGHCORenderContext>> {
		@Override
		protected void extend(TypeSwitch1.FunctionBuilder<Object, IExplicitRenderable<? super IGHCORenderContext>> builder) {
			builder.add(IExplicitGHCORenderable.class, e -> c -> e.render(c));
			ITextAppender.addToBuilder(builder, new ITextAppender.IExplicitFactory<IGHCORenderContext, IExplicitRenderable<? super IGHCORenderContext>>() {
				@Override
				public <T> IFunction1<? super T, ? extends IExplicitRenderable<? super IGHCORenderContext>> create(IConsumer2<? super IGHCORenderContext, ? super T> consumer) {
					return e -> c -> consumer.accept(c, e);
				}
			});

			builder.add(GHCodeOwners.class, e -> c -> {
				for (IGHCOLine line : e.getLines()) {
					c.render(line, IGHCOLine.class);
				}
			});
			builder.add(GHCOBlank.class, e -> c -> c.newline());
			builder.add(GHCOComment.class, e -> c -> c.append("# ").append(e.getComment()).newline());
			builder.add(GHCOPattern.class, e -> c -> {
				c.append(e.getPattern());
				for (String owner : e.getOwners()) {
					c.append(' ').append(owner);
				}
				c.newline();
			});
		}
	}

	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private static final IRendering<Object, IGHCORenderContext, IExplicitRenderable<? super IGHCORenderContext>> renderingStatic = new GHCORendering();

	@Override
	protected IGHCORenderContext createContext(TextNestedModified.TextNestedModifiedBuilder builder) {
		return new GHCORenderContext(builder);
	}

	@Override
	protected IRendering<? super Object, ? extends IGHCORenderContext, ? extends IExplicitRenderable<? super IGHCORenderContext>> getRendering() {
		return getRenderingStatic();
	}
}
