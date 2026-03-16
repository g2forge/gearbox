package com.g2forge.gearbox.serdes.xml;

import com.g2forge.gearbox.serdes.ATestSerdesFactory;
import com.g2forge.gearbox.serdes.SerdesFormat;

public class TestXMLSerdesFactory extends ATestSerdesFactory {
	@Override
	protected SerdesFormat getFormat() {
		return SerdesFormat.XML;
	}
}
