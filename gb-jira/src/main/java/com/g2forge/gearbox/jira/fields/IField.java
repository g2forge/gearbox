package com.g2forge.gearbox.jira.fields;

import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.g2forge.alexandria.java.adt.name.IStringNamed;

public interface IField extends IStringNamed {
	public default FieldInput createFieldInput(Object value) {
		final Object fieldInputValue;
		final String withKey = getWithKey();
		if (withKey == null) fieldInputValue = value;
		else fieldInputValue = ComplexIssueInputFieldValue.with(withKey, value);
		return new FieldInput(getName(), fieldInputValue);
	}

	/**
	 * Get the {@link ComplexIssueInputFieldValue} key to use when converting values for this field into REST API objects to send to the server.
	 * 
	 * @return The {@link ComplexIssueInputFieldValue} key.
	 */
	public String getWithKey();
}
