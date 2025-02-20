package com.example.AutumnMall.batch.config;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.repository.CartItemJdbcRepository;
import com.example.AutumnMall.batch.listener.CartItemBatchListener;
import com.example.AutumnMall.batch.processor.OldCartItemProcessor;
import com.example.AutumnMall.batch.reader.OldCartItemReader;
import com.example.AutumnMall.batch.writer.OldCartItemWriter;
import com.example.AutumnMall.exception.BusinessLogicException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CartItemJdbcRepository cartItemJdbcRepository;

    public BatchConfig(JobBuilderFactory jobBuilderFactory,
                       StepBuilderFactory stepBuilderFactory,
                       CartItemJdbcRepository cartItemJdbcRepository) { // 생성자 주입
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.cartItemJdbcRepository = cartItemJdbcRepository; // CartItemJdbcRepository 할당
    }

    @Bean
    public Job cartItemBatchJob(Step cartItemDeleteStep) {
        return jobBuilderFactory.get("cartItemBatchJob")
                .start(cartItemDeleteStep) // 첫 번째 스텝으로 cartItemDeleteStep을 설정
                .build();
    }

    @Bean
    public Step cartItemDeleteStep() {
        return stepBuilderFactory.get("cartItemDeleteStep")
                .<CartItem, CartItem>chunk(10)
                .reader(oldCartItemReader())
                .processor(oldCartItemProcessor())
                .writer(oldCartItemWriter())
                .faultTolerant() // 오류 발생 시 fault tolerance 적용
                .retry(NullPointerException.class) // Retry NullPointerException
                .retryLimit(5)
                .skip(NullPointerException.class)  // Skip NullPointerException
                .skipLimit(10)  // 최대 10번까지 Skip 가능
                .listener(new CartItemBatchListener()) // 배치 리스너
                .build();
    }

    // OldCartItemReader와 OldCartItemWriter 빈으로 설정
    @Bean("customOldCartItemReader")
    public OldCartItemReader oldCartItemReader() {
        return new OldCartItemReader(cartItemJdbcRepository); // OldCartItemReader 클래스 정의
    }

    @Bean
    public OldCartItemWriter oldCartItemWriter() {
        return new OldCartItemWriter(cartItemJdbcRepository); // OldCartItemWriter 클래스 정의
    }

    @Bean
    public OldCartItemProcessor oldCartItemProcessor(){
        return new OldCartItemProcessor(cartItemJdbcRepository);
    }
}