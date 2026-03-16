package com.g2forge.gearbox.serdes.json;

import com.g2forge.gearbox.serdes.ATestSerdesFactory;
import com.g2forge.gearbox.serdes.SerdesFormat;

public class TestJSONSerdesFactory extends ATestSerdesFactory {
	@Override
	protected SerdesFormat getFormat() {
		return SerdesFormat.JSON;
	}
}
