package br.com.devcave.mybank.repository;

import br.com.devcave.mybank.domain.entity.Bank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface BankRepository extends CrudRepository<Bank, Long> {
    Boolean existsByIban(String iban);

    Page<Bank> findBy(Pageable pageable);
}
