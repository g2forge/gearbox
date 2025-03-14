package com.g2forge.gearbox.jira.fields;

import com.atlassian.jira.rest.client.api.domain.IssueFieldId;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum KnownField implements IField {
	Assignee(IssueFieldId.ASSIGNEE_FIELD.id, "name"),
	EpicSummary("customfield_10002", null),
	Parent("customfield_10000", null),
	Sprint("customfield_10004", null),
	Security("security", "name"),
	Status("status", null);

	protected final String name;

	protected final String withKey;

	public IField get(IFieldConfig fieldConfig) {
		if ((fieldConfig == null) || (fieldConfig.getFields() == null)) return this;
		final Field retVal = fieldConfig.getFields().get(this);
		if (retVal == null) return this;
		return retVal;
	}
}
