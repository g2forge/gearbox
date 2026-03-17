package com.g2forge.gearbox.issue.serdes;

import com.g2forge.gearbox.serdes.SerdesFormat;

public class TestXMLIssueSerdes extends ATestIssueSerdes {
	@Override
	protected SerdesFormat getFormat() {
		return SerdesFormat.XML;
	}
}
