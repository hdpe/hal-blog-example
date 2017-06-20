package me.hdpe.halblog.client.rt.model;

import java.math.BigDecimal;

public class Account {
	
	private BigDecimal creditLimit;

	public BigDecimal getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}
}
