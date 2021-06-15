package br.com.devcave.mybank.exception;

import org.springframework.http.HttpStatus;

public class TransferLimitExceededException extends MyBankException {

    public TransferLimitExceededException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Transfer limit exceeded");
    }
}
