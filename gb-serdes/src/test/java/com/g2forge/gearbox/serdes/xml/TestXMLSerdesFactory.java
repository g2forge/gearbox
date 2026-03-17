package com.g2forge.gearbox.serdes.xml;

import com.g2forge.gearbox.serdes.AStringTestSerdesFactory;
import com.g2forge.gearbox.serdes.SerdesFormat;

public class TestXMLSerdesFactory extends AStringTestSerdesFactory {
	@Override
	protected SerdesFormat getFormat() {
		return SerdesFormat.XML;
	}
}
