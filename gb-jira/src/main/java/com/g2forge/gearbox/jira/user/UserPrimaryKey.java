package com.g2forge.gearbox.jira.user;

import com.atlassian.jira.rest.client.api.domain.BasicUser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserPrimaryKey {
	NAME("key") {
		@Override
		public String getValue(BasicUser user) {
			return user.getName();
		}
	},
	ACCOUNTID("accountId") {
		@Override
		public String getValue(BasicUser user) {
			return user.getAccountId();
		}
	};

	protected final String queryParameter;

	public abstract String getValue(BasicUser user);
}
