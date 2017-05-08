package hal.example.client.rt.model;

import java.util.Set;

import org.springframework.hateoas.Resource;

public class Customer {

	private String name;
	
	private Set<Resource<Account>> accounts;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Resource<Account>> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Resource<Account>> accounts) {
		this.accounts = accounts;
	}
}
