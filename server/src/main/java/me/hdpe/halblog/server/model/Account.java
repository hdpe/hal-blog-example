package me.hdpe.halblog.server.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	private Customer customer;
	
	@ManyToOne
	private AccountType type;
	
	private BigDecimal creditLimit;

	@SuppressWarnings("unused")
	private Account() {
		// for JPA
	}
	
	public Account(Customer customer, AccountType type, BigDecimal creditLimit) {
		this.customer = customer;
		this.type = type;
		this.creditLimit = creditLimit;
	}

	public int getId() {
		return id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public AccountType getType() {
		return type;
	}
	
	public BigDecimal getCreditLimit() {
		return creditLimit;
	}
}
