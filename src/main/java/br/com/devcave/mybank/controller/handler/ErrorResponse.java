package br.com.devcave.mybank.controller.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String traceId;

    private final List<String> errors;

    private final String path;
}
