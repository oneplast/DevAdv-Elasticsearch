package io.eddie.demo.domain.carts.service.handler;

import io.eddie.core.commands.CancelCreateCartCommand;
import io.eddie.core.commands.CreateCartCommand;
import io.eddie.core.events.CartCreateCancelledEvent;
import io.eddie.core.events.CartCreateFailedEvent;
import io.eddie.core.events.CartCreatedEvent;
import io.eddie.demo.domain.carts.model.entity.Cart;
import io.eddie.demo.domain.carts.service.CartService;
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
@KafkaListener( topics = {
        "${carts.command.topic.name}"
})
@RequiredArgsConstructor
public class CartsCommandsHandler {

    @Value("${carts.event.topic.name}")
    private String cartsEventsTopicName;

    private final CartService cartService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaHandler
    public void handleCommand(
            @Payload CreateCartCommand command
    ) {

        try {

            Cart savedCart = cartService.save(command.accountCode());

            CartCreatedEvent event = new CartCreatedEvent(command.accountCode(), savedCart.getCode());

            kafkaTemplate.send(cartsEventsTopicName, event);


        } catch (Exception e) {

            log.error("장바구니 처리하는데 오류가 발생했습니다. ", e);

            CartCreateFailedEvent failedEvent = new CartCreateFailedEvent(command.accountCode());

            kafkaTemplate.send(cartsEventsTopicName, failedEvent);

        }

    }

    @KafkaHandler
    public void handleCommand(@Payload CancelCreateCartCommand command) {

        cartService.deleteCart(command.accountCode());

        CartCreateCancelledEvent cartCreateCancelledEvent = new CartCreateCancelledEvent(command.accountCode());

        kafkaTemplate.send(cartsEventsTopicName, cartCreateCancelledEvent);

    }

}
