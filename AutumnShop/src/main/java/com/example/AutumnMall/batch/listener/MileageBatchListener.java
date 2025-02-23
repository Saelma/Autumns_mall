package com.example.AutumnMall.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MileageBatchListener extends StepExecutionListenerSupport {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("배치 작업 시작, START: {}", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getStatus().isUnsuccessful()) {
            log.error("배치 작업 실패, FAILED: {}", stepExecution.getStepName());
            return ExitStatus.FAILED;
        }
        log.info("배치 작업 성공, SUCCESS: {}", stepExecution.getStepName());
        return ExitStatus.COMPLETED;
    }
}
