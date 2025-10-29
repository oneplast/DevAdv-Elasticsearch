package io.eddie.core.events;

public record DepositCreateFailedEvent(
    String accountCode
) {
}
