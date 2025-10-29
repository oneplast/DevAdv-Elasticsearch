package io.eddie.accountservice.saga;

import io.eddie.accountservice.service.AccountService;
import io.eddie.core.commands.*;
import io.eddie.core.events.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        topics = {
            "${accounts.events.topic.name}",
            "${carts.events.topic.name}",
            "${deposits.events.topic.name}"
        }
)
@RequiredArgsConstructor
public class AccountSaga {

    private final AccountService accountService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${carts.commands.topic.name}")
    private String cartsCommandTopicName;

    @Value("${deposits.commands.topic.name}")
    private String depositsCommandTopicName;

    @Value("${accounts.commands.topic.name}")
    private String accountsCommandTopicName;

    @KafkaHandler
    public void handleEvent(@Payload AccountCreatedEvent event) {

        CreateCartCommand createCartCommand = new CreateCartCommand(event.accountCode());

        kafkaTemplate.send(cartsCommandTopicName, createCartCommand);

    }

    @KafkaHandler
    public void handleEvent(@Payload CartCreatedEvent event) {

        accountService.applyCartCode(event.accountCode(), event.cartCode());

        CreateDepositCommand createDepositCommand = new CreateDepositCommand(event.accountCode());

        kafkaTemplate.send(depositsCommandTopicName, createDepositCommand);

    }

    @KafkaHandler
    public void handleEvent(@Payload DepositCreatedEvent event) {

        accountService.applyDepositCode(event.accountCode(), event.depositCode());

        NotifyCreatedAccountAlertCommand notifyCreatedAccountAlertCommand = new NotifyCreatedAccountAlertCommand(event.accountCode());

        kafkaTemplate.send(accountsCommandTopicName, notifyCreatedAccountAlertCommand);

    }

    @KafkaHandler
    public void handleEvent(@Payload DepositCreateFailedEvent event) {

        CancelCreateCartCommand cancelCreateCartCommand = new CancelCreateCartCommand(event.accountCode());

        kafkaTemplate.send(cartsCommandTopicName, cancelCreateCartCommand);

    }

    @KafkaHandler
    public void handleEvent(@Payload CartCreateFailedEvent event) {

        NotifyAccountCreateFailedAlertCommand notifyAccountCreateFailedAlertCommand = new NotifyAccountCreateFailedAlertCommand(event.accountCode());

        kafkaTemplate.send(accountsCommandTopicName, notifyAccountCreateFailedAlertCommand);

    }

    @KafkaHandler
    public void handleEvent(@Payload CartCreateCancelledEvent event) {

        NotifyAccountCreateFailedAlertCommand notifyAccountCreateFailedAlertCommand = new NotifyAccountCreateFailedAlertCommand(event.accountCode());

        kafkaTemplate.send(accountsCommandTopicName, notifyAccountCreateFailedAlertCommand);

    }

}
