package com.g2forge.gearbox.functional.proxy;

import com.g2forge.gearbox.functional.runner.IRunner;

public interface IProxifier {
	public <T> T generate(IRunner runner, Class<T> type);
}
