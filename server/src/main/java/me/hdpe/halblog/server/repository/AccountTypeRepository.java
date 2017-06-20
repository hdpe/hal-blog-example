package me.hdpe.halblog.server.repository;

import org.springframework.data.repository.CrudRepository;

import me.hdpe.halblog.server.model.AccountType;

public interface AccountTypeRepository extends CrudRepository<AccountType, Integer> {
	// no additional methods
}
