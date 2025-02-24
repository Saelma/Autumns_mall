package com.example.AutumnMall.Member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MileageBatchService {

    private final JobLauncher jobLauncher;
    private final Job mileageExpirationJob;

    public boolean runMileageExpiration() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(mileageExpirationJob, jobParameters);
            return true;
        } catch (Exception e) {
            log.error("Mileage expiration batch job failed.", e);
            return false;
        }
    }
}
