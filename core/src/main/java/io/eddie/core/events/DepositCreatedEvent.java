package io.eddie.core.events;

public record DepositCreatedEvent(
        String accountCode,
        String depositCode
) {
}
