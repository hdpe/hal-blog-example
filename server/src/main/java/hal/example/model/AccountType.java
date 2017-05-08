package hal.example.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AccountType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;
	
	private AccountType() {
	}
	
	public AccountType(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}
