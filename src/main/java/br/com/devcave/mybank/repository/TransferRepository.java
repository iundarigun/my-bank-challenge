package br.com.devcave.mybank.repository;

import br.com.devcave.mybank.domain.entity.Transfer;
import org.springframework.data.repository.CrudRepository;

public interface TransferRepository extends CrudRepository<Transfer, Long> {
}
