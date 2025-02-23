package com.example.AutumnMall.batch.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Slf4j
@Configuration
@EnableScheduling
public class BatchScheduler {

    @Autowired
    private final JobLauncher jobLauncher;

    @Autowired
    private final Job cartItemBatchJob; // BatchConfig에서 정의한 Job

    @Autowired
    private final Job mileageExpireJob;

    @Autowired
    public BatchScheduler(JobLauncher jobLauncher,
                          Job cartItemBatchJob,
                          Job mileageExpireJob) {
        this.jobLauncher = jobLauncher;
        this.cartItemBatchJob = cartItemBatchJob;
        this.mileageExpireJob = mileageExpireJob;
    }

    // 매일 자정에 배치 작업 실행 (0 0 0 * * *)
    @Scheduled(cron = "0 0 0 * * *")
    public void executeBatchJob() throws Exception {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("timestamp", new Date()) // 실행할 때마다 새로운 파라미터 추가
                    .addLong("id", 1L)
                    .toJobParameters();

            JobParameters jobParameters2 = new JobParametersBuilder()
                    .addDate("timestamp", new Date()) // 실행할 때마다 새로운 파라미터 추가
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(cartItemBatchJob, jobParameters);
            JobExecution execution2 = jobLauncher.run(mileageExpireJob, jobParameters2);
            System.out.println("Batch job 실행 상태: " + execution.getStatus());
            System.out.println("Batch job 실행 상태: " + execution2.getStatus());
        } catch (JobExecutionException e) {
            log.error("자정에 배치 작업을 하는 데 실패했습니다!");
        } catch (Exception e){
            log.error("배치 작업 실행 중 예외가 발생했습니다.");
        }
    }
}
