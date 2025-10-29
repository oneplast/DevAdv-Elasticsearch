package io.eddie.accountservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfiguration {

    @Value("${accounts.config.topic-partitions}")
    private int topic_partitions;

    @Value("${accounts.config.topic-replications}")
    private int topic_replications;

    @Value("${accounts.events.topic.name}")
    private String accountsEventsTopicName;

    @Value("${carts.commands.topic.name}")
    private String cartsCommandsTopicName;

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic createAccountsEventTopic() {
        return TopicBuilder.name(accountsEventsTopicName)
                .partitions(topic_partitions)
                .replicas(topic_replications)
                .build();
    }

    @Bean
    public NewTopic createCartsCommandsTopic() {
        return TopicBuilder.name(cartsCommandsTopicName)
                .partitions(topic_partitions)
                .replicas(topic_replications)
                .build();
    }

}
