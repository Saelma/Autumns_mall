package com.example.AutumnMall.batch.config;

import com.example.AutumnMall.Member.domain.Mileage;
import com.example.AutumnMall.Member.repository.MileageJdbcRepository;
import com.example.AutumnMall.batch.listener.MileageBatchListener;
import com.example.AutumnMall.batch.processor.MileageItemProcessor;
import com.example.AutumnMall.batch.reader.MileageItemReader;
import com.example.AutumnMall.batch.writer.MileageItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableBatchProcessing
public class MileageExpireBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MileageJdbcRepository mileageJdbcRepository;

    public MileageExpireBatchConfig(JobBuilderFactory jobBuilderFactory,
                                    StepBuilderFactory stepBuilderFactory,
                                    MileageJdbcRepository mileageJdbcRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.mileageJdbcRepository = mileageJdbcRepository;
    }

    // @Primary ( 테스트 시 해당 어노테이션 활성화
    @Bean("mileageExpireJob")
    @Qualifier("mileageExpireJob")
    public Job mileageExpireJob(Step mileageExpireStep) {
        return jobBuilderFactory.get("mileageExpireJob")
                .start(mileageExpireStep)
                .build();
    }

    @Bean
    public Step mileageExpireStep() {
        return stepBuilderFactory.get("mileageExpireStep")
                .<Mileage, Mileage>chunk(10)
                .reader(mileageItemReader())
                .processor(mileageItemProcessor())
                .writer(mileageItemWriter())
                .faultTolerant() // 오류 발생 시 fault tolerance 적용
                .retry(Exception.class) // Retry Exception
                .retryLimit(3)
                .skip(Exception.class)  // Skip Exception
                .skipLimit(5)  // 최대 10번까지 Skip 가능
                .listener(new MileageBatchListener())
                .build();
    }

    @Bean
    public MileageItemProcessor mileageItemProcessor() {
        return new MileageItemProcessor();
    }

    @Bean
    public MileageItemWriter mileageItemWriter() {
        return new MileageItemWriter(mileageJdbcRepository);
    }

    @Bean
    public MileageItemReader mileageItemReader() {
        return new MileageItemReader(mileageJdbcRepository);
    }
}
