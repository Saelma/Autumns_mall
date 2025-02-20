package com.example.AutumnMall.batch.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Service
public class BatchService {

    private final JobLauncher jobLauncher;
    private final Job cartItemBatchJob;

    public BatchService(JobLauncher jobLauncher, Job cartItemBatchJob) {
        this.jobLauncher = jobLauncher;
        this.cartItemBatchJob = cartItemBatchJob;
    }

    public void runBatchJob() {
        try {
            jobLauncher.run(cartItemBatchJob, new JobParameters());
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }
}
