package com.example.AutumnMall.batch.processor;

import com.example.AutumnMall.Member.domain.Mileage;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import lombok.Setter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.ExitStatus;

@Setter
public class MileageItemProcessor implements ItemProcessor<Mileage, Mileage>, StepExecutionListener {

    private StepExecution stepExecution;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (!stepExecution.getFailureExceptions().isEmpty()) {
            return ExitStatus.FAILED;
        }
        return ExitStatus.COMPLETED;
    }

    @Override
    public Mileage process(Mileage mileage) throws Exception {
        if (mileage == null || mileage.getAmount() <= 0) {
            throw new BusinessLogicException(ExceptionCode.MILEAGE_NOT_FOUND);
        }
        return mileage;
    }
}
