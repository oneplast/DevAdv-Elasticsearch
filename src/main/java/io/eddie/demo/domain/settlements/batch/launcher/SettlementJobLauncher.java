package io.eddie.demo.domain.settlements.batch.launcher;

import io.eddie.demo.domain.settlements.utils.SettlementTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementJobLauncher {

    private final JobLauncher jobLauncher;
    private final Job settlementJob;

    public void run() throws Exception {

        log.info("Starting settlement job");

        // 전월(Previous Month) 문자열 불러오기
//        String previousDate = SettlementTimeUtils.getPreviousMonthStr();

        // 테스트를 위한 이번달 문자열 불러오기
        String previousDate = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM")
        );

        log.info("previousDate = {}", previousDate);

        JobParameters parameters = new JobParametersBuilder()
                .addString("dateStr", previousDate)
                .toJobParameters();

        jobLauncher.run(settlementJob, parameters);

    }


}
