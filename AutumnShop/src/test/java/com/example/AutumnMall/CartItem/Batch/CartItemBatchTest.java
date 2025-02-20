package com.example.AutumnMall.CartItem.Batch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Slf4j
@SpringBatchTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CartItemBatchTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void 배치_정상_실행_테스트() throws Exception {
        // Given
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis()) // 각 실행마다 고유한 파라미터 부여
                .toJobParameters();

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

    }

    @Test
    void 배치_실패_및_롤백_테스트() throws Exception {
        // Given - 실패를 유도하는 파라미터 추가 (예: 존재하지 않는 CartItem ID)
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("id", -100L) // 존재하지 않는 ID를 전달
                .addLong("timestamp", System.currentTimeMillis())  // 시간을 추가하여 파라미터를 변경
                .toJobParameters();

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);

    }

    @Test
    void 배치_로그_파일_저장_테스트() throws Exception {
        // Given
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // When
        jobLauncherTestUtils.launchJob(jobParameters);

        // Then - 로그 파일이 정상적으로 기록되었는지 확인
        String logFilePath = "logs/batch/batch_log.log";
        File logFile = new File(logFilePath);

        assertThat(logFile.exists()).isTrue(); // 파일 존재 여부 확인
        String logContent = new String(Files.readAllBytes(Paths.get(logFilePath)));
        assertThat(logContent).contains("배치 작업 시작"); // 로그 내용 확인

    }
}
