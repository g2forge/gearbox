package com.g2forge.gearbox.serdes.yaml;

import com.g2forge.gearbox.serdes.ATestSerdesFactory;
import com.g2forge.gearbox.serdes.SerdesFormat;

public class TestYAMLSerdesFactory extends ATestSerdesFactory {
	@Override
	protected SerdesFormat getFormat() {
		return SerdesFormat.YAML;
	}
}
