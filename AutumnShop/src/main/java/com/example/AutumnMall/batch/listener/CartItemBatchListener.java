package com.example.AutumnMall.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

@Slf4j
public class CartItemBatchListener extends StepExecutionListenerSupport {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("배치 작업 시작: {}", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("배치 작업 완료: {}", stepExecution.getStepName());
        } else {
            log.error("배치 작업 실패: {}", stepExecution.getStepName());
            return ExitStatus.FAILED;
        }
        return ExitStatus.COMPLETED;
    }
}
