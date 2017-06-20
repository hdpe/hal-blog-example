package me.hdpe.halblog.client.rt;

import java.util.Arrays;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.hdpe.halblog.client.rt.model.Account;
import me.hdpe.halblog.client.rt.model.AccountType;
import me.hdpe.halblog.client.rt.model.Customer;

public class TotalCreditLimit {

	public static void main(String[] args) {
		
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
				.modules(new Jackson2HalModule())
				.build();

		RestTemplate restTemplate = new RestTemplate(Arrays.asList(
				new MappingJackson2HttpMessageConverter(objectMapper)));
		
		Customer customer = restTemplate.getForObject("http://localhost:8080/customers/1",
				Customer.class);
		
		for (Resource<Account> accountResource : customer.getAccounts()) {
			Account account = accountResource.getContent();
			Link accountTypeLink = accountResource.getLink("type");
			
			AccountType accountType = restTemplate.getForObject(accountTypeLink.getHref(), AccountType.class);
			
			System.out.format("%s: credit limit £%.2f%n", accountType.getName(), account.getCreditLimit());
		}
	}
}
