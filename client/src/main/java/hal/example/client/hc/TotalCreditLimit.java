package hal.example.client.hc;

import hal.example.client.hc.model.Account;
import hal.example.client.hc.model.Customer;

import java.net.URI;

import uk.co.blackpepper.halclient.Client;
import uk.co.blackpepper.halclient.ClientFactory;
import uk.co.blackpepper.halclient.Configuration;

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
