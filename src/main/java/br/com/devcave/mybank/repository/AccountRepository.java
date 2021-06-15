package br.com.devcave.mybank.repository;

import br.com.devcave.mybank.domain.entity.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Boolean existsByOwnerIdAndBankId(Long ownerId, Long bankId);
}
