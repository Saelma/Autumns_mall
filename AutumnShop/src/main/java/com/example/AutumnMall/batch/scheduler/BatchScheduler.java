package com.example.AutumnMall.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class BatchScheduler {

    @Autowired
    private final JobLauncher jobLauncher;

    @Autowired
    private final Job cartItemBatchJob; // BatchConfig에서 정의한 Job

    @Autowired
    public BatchScheduler(JobLauncher jobLauncher, Job cartItemBatchJob) {
        this.jobLauncher = jobLauncher;
        this.cartItemBatchJob = cartItemBatchJob;
    }

    // 매일 자정에 배치 작업 실행 (0 0 0 * * *)
    @Scheduled(cron = "0 0 0 * * *")
    public void executeBatchJob() throws Exception {
        jobLauncher.run(cartItemBatchJob, new JobParameters());
    }
}
