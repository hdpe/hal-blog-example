package me.hdpe.halblog.server.repository;

import org.springframework.data.repository.CrudRepository;

import me.hdpe.halblog.server.model.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {
	// no additional methods
}
