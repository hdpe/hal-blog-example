package me.hdpe.halblog.client.bowman;

import java.net.URI;

import me.hdpe.halblog.client.bowman.model.Account;
import me.hdpe.halblog.client.bowman.model.Customer;

import uk.co.blackpepper.bowman.Client;
import uk.co.blackpepper.bowman.ClientFactory;
import uk.co.blackpepper.bowman.Configuration;

public class TotalCreditLimit {

	public static void main(String[] args) {
		
		ClientFactory clientFactory = Configuration.build().buildClientFactory();
		
		Client<Customer> customerClient = clientFactory.create(Customer.class);
				
		Customer customer = customerClient.get(URI.create("http://localhost:8080/customers/1"));
		
		for (Account account : customer.getAccounts()) {
			System.out.format("%s: credit limit £%.2f%n", account.getType().getName(),
					account.getCreditLimit());
		}
	}
}
