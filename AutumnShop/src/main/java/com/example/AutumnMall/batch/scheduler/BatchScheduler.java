package com.example.AutumnMall.batch.scheduler;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

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
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("timestamp", new Date()) // 실행할 때마다 새로운 파라미터 추가
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(cartItemBatchJob, jobParameters);
            System.out.println("Batch job 실행 상태: " + execution.getStatus());
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }
}
