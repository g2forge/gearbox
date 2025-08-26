package com.g2forge.gearbox.ssh.agent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.io.file.TempDirectory;
import com.g2forge.alexandria.java.text.quote.BashQuoteType;
import com.g2forge.alexandria.java.text.quote.QuoteControl;
import com.g2forge.gearbox.command.converter.dumb.Command;
import com.g2forge.gearbox.command.converter.dumb.Constant;
import com.g2forge.gearbox.command.converter.dumb.ConstantEnvironment;
import com.g2forge.gearbox.command.converter.dumb.Environment;
import com.g2forge.gearbox.command.converter.dumb.Working;
import com.g2forge.gearbox.command.proxy.ICommandProxyFactory;
import com.g2forge.gearbox.command.proxy.method.ICommandInterface;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class SSHAgentFactory {
	protected static interface ISSHAgentCommand extends ICommandInterface {
		@Command({})
		public Stream<String> start(@Constant("-s") Path executable);

		@Command({})
		public Stream<String> stop(@Environment(ISSHAgent.SSH_AGENT_PID) String pid, @Constant("-k") Path executable);

		@Command({})
		@ConstantEnvironment(variable = "DISPLAY", value = "0")
		public boolean add(@Environment(ISSHAgent.SSH_AUTH_SOCK) String socket, @Environment("SSH_ASKPASS") String askpass, @Working Path working, Path executable, Path key);
	}

	protected static final Pattern PATTERN_SSH_ENV_VAR_LINE = Pattern.compile("(?<key>SSH_[A-Z_]+)=(?<value>[^;]+);.*");

	protected final ISSHAgentInstallation installation;

	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private final Path sshAgentExecutable = getInstallation().getSSHAgentExecutable();

	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private final Path sshAddExecutable = getInstallation().getSSHAddExecutable();

	protected final ICommandProxyFactory commandProxyFactory;

	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private final ISSHAgentCommand command = getCommandProxyFactory().apply(ISSHAgentCommand.class);

	public ISSHAgent create() {
		final Map<String, String> map = new LinkedHashMap<>();
		final ISSHAgentCommand command = getCommand();
		command.start(getSshAgentExecutable()).forEach(line -> {
			final Matcher matcher = PATTERN_SSH_ENV_VAR_LINE.matcher(line);
			if (!matcher.matches()) return;
			map.put(matcher.group("key"), matcher.group("value"));
		});

		final String socketUntranslated = map.get("SSH_AUTH_SOCK"), socketTranslated = getInstallation().translateSocket(socketUntranslated);
		final String pid = map.get("SSH_AGENT_PID");

		return new ASSHAgent(socketTranslated, pid) {
			@Override
			protected void closeInternal() {
				command.stop(getPid(), getSshAgentExecutable());
			}

			@Override
			public boolean add(Path key, String passphrase) {
				try (final TempDirectory temp = new TempDirectory()) {
					final Path askpass = temp.get().resolve("askpass.sh");
					Files.writeString(askpass, "#!/bin/bash\necho " + BashQuoteType.BashDoubleExpand.quoteAny(QuoteControl.Always, passphrase) + "\n");
					return command.add(socketUntranslated, "./" + askpass.getFileName().toString(), temp.get(), getSshAddExecutable(), key);
				} catch (IOException e) {
					throw new RuntimeIOException(e);
				}
			}
		};
	}
}
