package me.hdpe.halblog.server;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.hdpe.halblog.server.model.AccountType;
import me.hdpe.halblog.server.model.Customer;
import me.hdpe.halblog.server.repository.AccountTypeRepository;
import me.hdpe.halblog.server.repository.CustomerRepository;

@SpringBootApplication
public class Application {
	
	@Autowired
	private AccountTypeRepository accountTypeRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@PostConstruct
	public void seed() {
		AccountType aardvantage = new AccountType("Aardvantage");
		AccountType platyPlus = new AccountType("PlatyPlus");
	
		accountTypeRepository.save(aardvantage);
		accountTypeRepository.save(platyPlus);
		
		Customer customer = new Customer("Jeremy Corbyn");
		customer.addAccount(aardvantage, BigDecimal.valueOf(2000.0));
		customer.addAccount(platyPlus, BigDecimal.valueOf(1000.0));
		
		customerRepository.save(customer);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
