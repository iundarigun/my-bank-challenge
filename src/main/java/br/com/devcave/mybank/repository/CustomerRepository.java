package br.com.devcave.mybank.repository;

import br.com.devcave.mybank.domain.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    Boolean existsByNationalIdentifier(String nationalIdentifier);
}
