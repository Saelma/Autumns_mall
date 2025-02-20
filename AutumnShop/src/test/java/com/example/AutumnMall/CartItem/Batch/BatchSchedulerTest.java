package com.example.AutumnMall.CartItem.Batch;

import com.example.AutumnMall.batch.scheduler.BatchScheduler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
public class BatchSchedulerTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job cartItemBatchJob;

    @InjectMocks
    private BatchScheduler batchScheduler;

    @Test
    void testExecuteBatchJob() throws Exception {
        // 배치 작업 실행 테스트
        batchScheduler.executeBatchJob();

        // JobLauncher의 run 메소드가 호출되었는지 확인
        verify(jobLauncher, times(1)).run(eq(cartItemBatchJob), any());
    }
}
