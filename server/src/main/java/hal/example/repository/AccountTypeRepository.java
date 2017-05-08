package hal.example.repository;

import hal.example.model.AccountType;

import org.springframework.data.repository.CrudRepository;

public interface AccountTypeRepository extends CrudRepository<AccountType, Integer> {
}
