package me.hdpe.halblog.client.bowman.model;

import java.util.Set;

import uk.co.blackpepper.bowman.InlineAssociationDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class Customer {

	private String name;
	
	private Set<Account> accounts;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonDeserialize(contentUsing = InlineAssociationDeserializer.class)
	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}
}
