package com.g2forge.gearbox.serdes.json;

import com.g2forge.gearbox.serdes.AStringTestSerdesFactory;
import com.g2forge.gearbox.serdes.SerdesFormat;

public class TestJSONSerdesFactory extends AStringTestSerdesFactory {
	@Override
	protected SerdesFormat getFormat() {
		return SerdesFormat.JSON;
	}
}
