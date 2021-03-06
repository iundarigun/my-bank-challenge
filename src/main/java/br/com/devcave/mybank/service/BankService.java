package br.com.devcave.mybank.service;

import br.com.devcave.mybank.domain.entity.Bank;
import br.com.devcave.mybank.domain.request.BankRequest;
import br.com.devcave.mybank.domain.response.BankResponse;
import br.com.devcave.mybank.domain.response.PageResponse;
import br.com.devcave.mybank.exception.EntityAlreadyExistsException;
import br.com.devcave.mybank.exception.EntityNotFoundException;
import br.com.devcave.mybank.mapper.BankMapper;
import br.com.devcave.mybank.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    private final BankMapper bankMapper;

    @Transactional
    public BankResponse create(@Valid final BankRequest request) {
        if (bankRepository.existsByIban(request.getIban())) {
            throw new EntityAlreadyExistsException();
        }
        final var bank = bankRepository.save(bankMapper.bankRequestToEntity(request));

        return bankMapper.bankEntityToResponse(bank);
    }

    @Transactional(readOnly = true)
    public PageResponse<BankResponse> findByParams(final Integer page, final Integer size) {
        final var result = bankRepository.findBy(PageRequest.of(page - 1, size));

        final var content = result.getContent()
                .stream()
                .map(bankMapper::bankEntityToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(page, result.getTotalPages(), result.getTotalElements(), content);
    }

    @Transactional(readOnly = true)
    public Bank getById(final Long id) {
        return bankRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
