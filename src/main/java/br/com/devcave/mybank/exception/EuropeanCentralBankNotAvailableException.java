package br.com.devcave.mybank.exception;

import org.springframework.http.HttpStatus;

public class EuropeanCentralBankNotAvailableException extends MyBankException {

    public EuropeanCentralBankNotAvailableException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "ECB is not available");
    }
}
