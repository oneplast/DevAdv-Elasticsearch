package io.eddie.demo.domain.settlements.scheduler;

import io.eddie.demo.domain.settlements.batch.launcher.SettlementJobLauncher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementJobScheduler {

    private final SettlementJobLauncher jobLauncher;

    /**
     * 초 분 시 일 월 요일
     * 0  0  9  15 *  *
     */
    // [적용] 매 월 15일 오전9시
//    @Scheduled(cron = "0 0 9 15 * *")
    // [테스트] 매 분
//    @Scheduled(cron = "0 * * * * *")
    public void executeSettlement() throws Exception {
        jobLauncher.run();
    }

}
