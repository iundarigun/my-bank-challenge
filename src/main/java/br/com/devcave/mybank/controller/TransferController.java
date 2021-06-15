package br.com.devcave.mybank.controller;

import br.com.devcave.mybank.domain.request.TransferRequest;
import br.com.devcave.mybank.domain.response.TransferResponse;
import br.com.devcave.mybank.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferResponse> create(@Valid @RequestBody final TransferRequest request) {
        final var response = transferService.create(request);

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .build(response.getId());

        return ResponseEntity.created(location).body(response);
    }
}
