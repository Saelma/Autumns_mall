package com.example.AutumnMall.batch.processor;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.repository.CartItemJdbcRepository;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import lombok.Setter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.batch.item.ItemProcessor;

import org.springframework.batch.core.StepExecution;

@Setter
public class OldCartItemProcessor implements ItemProcessor<CartItem, CartItem>, StepExecutionListener {

    private final CartItemJdbcRepository cartItemJdbcRepository;
    private JobParameters jobParameters;

    public OldCartItemProcessor(CartItemJdbcRepository cartItemJdbcRepository) {
        this.cartItemJdbcRepository = cartItemJdbcRepository;
    }


    @Override
    public void beforeStep(StepExecution stepExecution) {
        // JobExecution을 StepExecution에서 가져옴
        this.jobParameters = stepExecution.getJobParameters();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // 예외가 발생했을 경우 실패로 처리
        if (stepExecution.getFailureExceptions().size() > 0) {
            System.out.println(1);
            return ExitStatus.FAILED;
        }
        return ExitStatus.COMPLETED;
    }

    @Override
    public CartItem process(CartItem cartItem) throws Exception {
        Long id = jobParameters.getLong("id");

        if(id == -100){
            throw new BusinessLogicException(ExceptionCode.CARTITEM_NOT_FOUND);
        }

        // quantity가 0이면 예외를 던져서 재시도 및 오류 처리가 가능하게 함
        if (cartItem.getQuantity() == 0) {
            throw new BusinessLogicException(ExceptionCode.CARTITEM_NOT_FOUND);
        }
        return cartItem;
    }
}