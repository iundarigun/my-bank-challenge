package br.com.devcave.mybank.domain.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor(onConstructor = @__({@JsonCreator}))
public class TransferRequest {
    @NotNull
    private final Long originId;

    @NotNull
    private final Long destinationId;

    @NotNull
    private final BigDecimal amount;

    @AssertTrue(message = "transfer must be different origin and destination")
    private boolean isSameOriginAndDestination() {
        return (originId == null || !originId.equals(destinationId));
    }
}
