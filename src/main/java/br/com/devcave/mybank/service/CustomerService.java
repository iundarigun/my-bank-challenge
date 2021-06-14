package br.com.devcave.mybank.service;

import br.com.devcave.mybank.domain.request.CustomerRequest;
import br.com.devcave.mybank.domain.response.CustomerResponse;
import br.com.devcave.mybank.exception.EntityAlreadyExistsException;
import br.com.devcave.mybank.exception.EntityNotFoundException;
import br.com.devcave.mybank.mapper.CustomerMapper;
import br.com.devcave.mybank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerResponse create(final CustomerRequest request) {
        if (customerRepository.existsByNationalIdentifier(request.getNationalIdentifier())) {
            throw new EntityAlreadyExistsException();
        }
        final var customer = customerRepository.save(customerMapper.customerRequestToEntity(request));

        return customerMapper.customerEntityToResponse(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(final Long id) {
        final var customer = customerRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return customerMapper.customerEntityToResponse(customer);
    }
}
