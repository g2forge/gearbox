package com.g2forge.gearbox.serdes.yaml;

import com.g2forge.gearbox.serdes.AStringTestSerdesFactory;
import com.g2forge.gearbox.serdes.SerdesFormat;

public class TestYAMLSerdesFactory extends AStringTestSerdesFactory {
	@Override
	protected SerdesFormat getFormat() {
		return SerdesFormat.YAML;
	}
}
