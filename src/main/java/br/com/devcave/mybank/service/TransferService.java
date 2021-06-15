package br.com.devcave.mybank.service;

import br.com.devcave.mybank.domain.TransferType;
import br.com.devcave.mybank.domain.entity.Account;
import br.com.devcave.mybank.domain.request.TransferRequest;
import br.com.devcave.mybank.domain.response.TransferResponse;
import br.com.devcave.mybank.mapper.TransferMapper;
import br.com.devcave.mybank.repository.TransferRepository;
import br.com.devcave.mybank.service.executor.TransferExecutorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final AccountService accountService;

    private final TransferRepository transferRepository;

    private final TransferMapper transferMapper;

    private final TransferExecutorFactory transferExecutorFactory;

    @Transactional
    public TransferResponse create(final TransferRequest request) {
        final var origin = accountService.getById(request.getOriginId());
        final var destination = accountService.getById(request.getDestinationId());
        final var transferType = discoverTransferType(origin, destination);

        final var transfer = transferExecutorFactory
                .getTransferExecutor(transferType)
                .doTransfer(origin, destination, request.getAmount());

        accountService.update(transfer.getOrigin());
        accountService.update(transfer.getDestination());

        return transferMapper.transferEntityToResponse(transferRepository.save(transfer));
    }

    private TransferType discoverTransferType(final Account origin, final Account destination) {
        if (origin.getBank().getId().equals(destination.getBank().getId())) {
            return TransferType.SAME_BANK;
        }
        if (origin.getOwner().getId().equals(destination.getOwner().getId())) {
            return TransferType.SAME_CUSTOMER;
        }
        return TransferType.DIFFERENT_BANK;
    }
}
