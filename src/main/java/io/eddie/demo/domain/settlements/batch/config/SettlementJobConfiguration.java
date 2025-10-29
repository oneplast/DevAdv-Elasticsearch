package io.eddie.demo.domain.settlements.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Slf4j
@Configuration
public class SettlementJobConfiguration {

    @Bean
    @Qualifier("settlementJob")
    public Job settlementJob(
            JobRepository jobRepository,
            @Qualifier("prepareSettlementDataStep") Step prepareSettlementDataStep,
            @Qualifier("processSettlementStep") Step processSettlementStep
    ) {
        return new JobBuilder("settlementJob_" + UUID.randomUUID(), jobRepository)
                .start(prepareSettlementDataStep)
                .next(processSettlementStep)
                .build();
    }

}
