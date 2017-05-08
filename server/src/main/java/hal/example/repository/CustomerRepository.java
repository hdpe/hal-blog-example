package hal.example.repository;

import hal.example.model.Customer;

import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {
}
