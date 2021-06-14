package br.com.devcave.mybank.controller;

import br.com.devcave.mybank.domain.request.BankRequest;
import br.com.devcave.mybank.domain.response.BankResponse;
import br.com.devcave.mybank.domain.response.PageResponse;
import br.com.devcave.mybank.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.net.URI;

@Validated
@RestController
@RequestMapping("banks")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @PostMapping
    public ResponseEntity<BankResponse> create(@Valid @RequestBody final BankRequest request) {
        var response = bankService.create(request);

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .build(response.getId());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<BankResponse>> findAll(
            @RequestParam(required = false, defaultValue = "1")
            @Min(value = 1) final Integer page,
            @RequestParam(required = false, defaultValue = "20")
            @Min(value = 5)
            @Max(value = 50) final Integer size) {
        final var response = bankService.findByParams(page, size);

        return ResponseEntity.ok(response);
    }
}
