package com.g2forge.gearbox.command.proxy.result;

import org.junit.Assert;
import org.junit.Test;

import com.g2forge.alexandria.command.invocation.format.PassthroughCommandFormat;
import com.g2forge.alexandria.command.invocation.runner.ICommandRunner;
import com.g2forge.alexandria.java.function.ISupplier;
import com.g2forge.alexandria.java.platform.HPlatform;
import com.g2forge.gearbox.command.HStandardCommand;
import com.g2forge.gearbox.command.converter.dumb.Command;
import com.g2forge.gearbox.command.converter.dumb.Constant;
import com.g2forge.gearbox.command.converter.dumb.DumbCommandConverter;
import com.g2forge.gearbox.command.converter.dumb.ResultSupplier;
import com.g2forge.gearbox.command.process.ProcessBuilderRunner;
import com.g2forge.gearbox.command.proxy.CommandProxyFactory;
import com.g2forge.gearbox.command.proxy.method.ICommandInterface;
import com.g2forge.gearbox.command.proxy.process.ModifyProcessInvocationException;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;

public class TestStringResultSupplier {
	public static class ErrorStringResultSupplier extends StringResultSupplier {
		@Override
		protected String process(ISupplier<String> stdout, ISupplier<String> stderr) {
			return stderr.get();
		}
	}

	public interface IEcho extends ICommandInterface {
		public String echo(String string);

		@Command("echo")
		@StringResultSupplier.Whitespace(stripTrailing = true)
		@ResultSupplier(ErrorStringResultSupplier.class)
		public default String echoError(@Constant(">&2") String string) {
			throw new ModifyProcessInvocationException(processInvocation -> new ProcessInvocation<>(processInvocation.getCommandInvocation().toBuilder().format(PassthroughCommandFormat.create()).build(), processInvocation.getResultSupplier()));
		}

		@StringResultSupplier.Whitespace(newlineReplacement = "X")
		@Command("echo")
		public String echoNewlineReplacement(String string);

		@StringResultSupplier.Whitespace(stripLeading = true, stripTrailing = true)
		@Command("echo")
		public String echoStripBoth(String string);

		@StringResultSupplier.Whitespace(stripLeading = true)
		@Command("echo")
		public String echoStripLeading(String string);

		@StringResultSupplier.Whitespace(stripTrailing = true)
		@Command("echo")
		public String echoStripTrailing(String string);
	}

	protected static final IEcho echoNative = HStandardCommand.getCommandProxyFactoryNative().apply(IEcho.class);
	
	protected static final IEcho echoShell = new CommandProxyFactory(DumbCommandConverter.create(), new ProcessBuilderRunner(ICommandRunner.create(HPlatform.getPlatform().getShell()))).apply(IEcho.class);

	@Test
	public void echo() {
		Assert.assertEquals(" A \n B \n", echoNative.echo(" A \n B "));
	}

	@Test
	public void echoError() {
		Assert.assertEquals("ERROR", echoShell.echoError("ERROR"));
	}

	@Test
	public void echoNewlineReplacement() {
		Assert.assertEquals(" A X B X", echoNative.echoNewlineReplacement(" A \n B "));
	}

	@Test
	public void echoStripBoth() {
		Assert.assertEquals("A \n B", echoNative.echoStripBoth(" A \n B "));
	}

	@Test
	public void echoStripLeading() {
		Assert.assertEquals("A \n B \n", echoNative.echoStripLeading(" A \n B "));
	}

	@Test
	public void echoStripTrailing() {
		Assert.assertEquals(" A \n B", echoNative.echoStripTrailing(" A \n B "));
	}
}
