package com.example.AutumnMall.Member.Batch;

import com.example.AutumnMall.Member.repository.MileageJdbcRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@Slf4j
@SpringBatchTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class MileageBatchTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    @Qualifier("mileageExpireJob")
    private Job mileageExpireJob;

    @Autowired
    private MileageJdbcRepository mileageJdbcRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void MileageExpirationBatch() throws Exception {
        assertThat(mileageExpireJob).isNotNull();
        assertThat(jobLauncherTestUtils.getJobLauncher()).isNotNull();

        // JobParameters를 생성하고 시간에 따라 다른 파라미터를 추가
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())  // 각 실행마다 새로운 파라미터
                .toJobParameters();

        // given: 테스트를 위한 가상 데이터 준비
        String insertSql = "INSERT INTO mileage (member_id, amount, type, description, date, expiration_date, remain_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // 예시로 만료된 마일리지를 추가
        jdbcTemplate.update(insertSql, 1L, 100, "ADD", "상품 구매 적립", LocalDate.now().minusDays(32), LocalDate.now().minusDays(31), 100);
        jdbcTemplate.update(insertSql, 2L, 200, "ADD", "상품 구매 적립", LocalDate.now().minusDays(30), LocalDate.now().minusDays(29), 200);

        // when: 배치 작업 실행
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then: 배치 작업이 완료된 후 검증
        assertThat(jobExecution).isNotNull();
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        // 만료된 마일리지가 처리되었는지 확인
        String sql = "SELECT id FROM mileage WHERE expiration_date < DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY) AND type = 'ADD'";
        List<Map<String, Object>> remainingMileage = jdbcTemplate.queryForList(sql);

        // 유효한 마일리지만 남아야 함
        assertEquals(0, remainingMileage.size());
    }

    @Test
    void MileageExpirationBatchNull() throws Exception {
        assertThat(mileageExpireJob).isNotNull();
        assertThat(jobLauncherTestUtils.getJobLauncher()).isNotNull();

        // JobParameters를 생성하고 시간에 따라 다른 파라미터를 추가
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())  // 각 실행마다 새로운 파라미터
                .toJobParameters();

        // given: 테스트를 위한 가상 데이터 준비
        String insertSql = "INSERT INTO mileage (member_id, amount, type, description, date, expiration_date, remain_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // when: 배치 작업 실행
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then: 배치 작업이 완료된 후 검증
        assertThat(jobExecution).isNotNull();
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        // 만료된 마일리지가 처리되었는지 확인
        String sql = "SELECT id FROM mileage WHERE expiration_date < DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY) AND type = 'ADD'";
        List<Map<String, Object>> remainingMileage = jdbcTemplate.queryForList(sql);

        // 유효한 마일리지만 남아야 함
        assertEquals(0, remainingMileage.size());
    }
}