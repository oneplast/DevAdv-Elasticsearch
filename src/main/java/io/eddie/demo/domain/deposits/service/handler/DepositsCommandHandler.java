package io.eddie.demo.domain.deposits.service.handler;

import io.eddie.core.commands.CreateDepositCommand;
import io.eddie.core.events.DepositCreateFailedEvent;
import io.eddie.core.events.DepositCreatedEvent;
import io.eddie.demo.domain.deposits.model.entity.Deposit;
import io.eddie.demo.domain.deposits.service.DepositService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(topics = {
        "${deposits.command.topic.name}"
})
@RequiredArgsConstructor
public class DepositsCommandHandler {

    private final DepositService depositService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${deposits.event.topic.name}")
    private String depositsEventTopicName;

    @KafkaHandler
    public void handleCommand(@Payload CreateDepositCommand command) {

        try {

            Deposit savedDeposit = depositService.save(command.accountCode());

            DepositCreatedEvent depositCreatedEvent = new DepositCreatedEvent(
                    command.accountCode(),
                    savedDeposit.getCode()
            );

            kafkaTemplate.send(depositsEventTopicName, depositCreatedEvent);

        } catch ( Exception e ) {

            log.error("예치금을 생성하는데 오류가 발생했습니다!", e);

            DepositCreateFailedEvent depositCreateFailedEvent = new DepositCreateFailedEvent(command.accountCode());

            kafkaTemplate.send(depositsEventTopicName, depositCreateFailedEvent);

        }

    }


}
