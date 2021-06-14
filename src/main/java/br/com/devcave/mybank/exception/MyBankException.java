package br.com.devcave.mybank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class MyBankException extends RuntimeException {
    @Getter
    private final HttpStatus status;

    public MyBankException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }
}
