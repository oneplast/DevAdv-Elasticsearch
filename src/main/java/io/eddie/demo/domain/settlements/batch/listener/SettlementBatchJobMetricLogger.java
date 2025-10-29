package io.eddie.demo.domain.settlements.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class SettlementBatchJobMetricLogger implements StepExecutionListener {

    private long start;

    @Override
    public void beforeStep(StepExecution se) {
        start = System.nanoTime();
    }

    @Override
    public ExitStatus afterStep(StepExecution se) {

        long durMs = (System.nanoTime()-start) / 1_000_000;

        log.info("BATCH_METRIC Step Name = {}, Read Count = {}, Write Count = {}, Commit Count = {}, Skip Count = {} During Milli Sec ={}",
                se.getStepName(), se.getReadCount(), se.getWriteCount(),
                se.getCommitCount(), se.getSkipCount(), durMs);

        return se.getExitStatus();

    }

}
