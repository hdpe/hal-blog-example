package me.hdpe.halblog.server.model;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private Set<Account> accounts = new LinkedHashSet<>();

	@SuppressWarnings("unused")
	private Customer() {
		// for JPA
	}
	
	public Customer(String name) {
		this.name = name;
	}
	
	public void addAccount(AccountType type, BigDecimal creditLimit) {
		accounts.add(new Account(this, type, creditLimit));
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Set<Account> getAccounts() {
		return accounts;
	}
}
