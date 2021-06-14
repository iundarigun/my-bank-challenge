package br.com.devcave.mybank.controller;

import br.com.devcave.mybank.domain.request.CustomerRequest;
import br.com.devcave.mybank.domain.response.CustomerResponse;
import br.com.devcave.mybank.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody final CustomerRequest request) {
        var response = customerService.create(request);

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .build(response.getId());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable final Long id) {
        final var response = customerService.findById(id);

        return ResponseEntity.ok(response);
    }
}
