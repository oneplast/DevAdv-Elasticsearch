package io.eddie.core.events;

public record CartCreatedEvent(
        String accountCode,
        String cartCode
) {
}
