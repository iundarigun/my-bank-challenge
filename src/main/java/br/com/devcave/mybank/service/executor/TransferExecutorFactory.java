package br.com.devcave.mybank.service.executor;

import br.com.devcave.mybank.domain.TransferType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TransferExecutorFactory {

    private final Map<TransferType, TransferExecutor> map;

    public TransferExecutorFactory(final List<TransferExecutor> list) {
        this.map = list.stream().collect(Collectors.toMap(TransferExecutor::getTransferType, (it) -> it));
    }

    public TransferExecutor getTransferExecutor(final TransferType transferType) {
        return map.get(transferType);
    }
}
