package hal.example.client.hc.model;

import java.math.BigDecimal;

import uk.co.blackpepper.halclient.annotation.LinkedResource;

public class Account {

	private AccountType type;
	
	private BigDecimal creditLimit;

	@LinkedResource
	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public BigDecimal getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}
}
