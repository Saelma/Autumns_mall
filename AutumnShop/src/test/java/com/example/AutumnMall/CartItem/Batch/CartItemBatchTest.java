package com.example.AutumnMall.CartItem.Batch;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.repository.CartItemJdbcRepository;
import com.example.AutumnMall.batch.reader.OldCartItemReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.item.ItemReader;
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
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Slf4j
@SpringBatchTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CartItemBatchTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job cartItemBatchJob;

    @Autowired
    private CartItemJdbcRepository cartItemJdbcRepository;

    //밑의 장바구니를 기준으로 배치 테스트 완료했으니, 해당 테스트는 필요없습니다. ( 장바구니 추가 테스트와 같음 )
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

    @Test
    void 장바구니_30일_지난_아이템_삭제_테스트() throws Exception {
        assertThat(cartItemBatchJob).isNotNull();
        assertThat(jobLauncherTestUtils.getJobLauncher()).isNotNull();

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis()) // 각 실행마다 새로운 파라미터
                .addLong("id", 1L)
                .toJobParameters();

        // When
        JobExecution jobExecution = jobLauncherTestUtils.getJobLauncher().run(cartItemBatchJob, jobParameters);

        // Then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    // OldCartItemReader에서 id 체크를 안 했지만 ,id 체크를 넣으면 성공적으로 됩니다.
    @Test
    void 장바구니_30일_지난_아이템_삭제_실패_테스트() throws Exception {
        assertThat(cartItemBatchJob).isNotNull();
        assertThat(jobLauncherTestUtils.getJobLauncher()).isNotNull();

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis()) // 각 실행마다 새로운 파라미터
                .addLong("id", -1L)
                .toJobParameters();

        // When
        JobExecution jobExecution = jobLauncherTestUtils.getJobLauncher().run(cartItemBatchJob, jobParameters);

        // Then: 배치 작업이 실패했을 경우
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
    }

    @Test
    void testCartItemReader_nullCartItem() throws Exception {
        // given
        ItemReader<CartItem> reader = new OldCartItemReader(cartItemJdbcRepository); // 실제 ItemReader 생성
        Iterator<CartItem> cartItemIterator = mock(Iterator.class);
        when(cartItemIterator.hasNext()).thenReturn(true, false); // 한번은 존재하고, 그 후에는 없다고 설정
        when(cartItemIterator.next()).thenReturn(null); // null을 반환

        // when
        CartItem result = reader.read();

        // then
        assertThat(result).isNull(); // null 반환 확인
    }

    @Test
    void testCartItemReader_zeroQuantity() throws Exception {
        // given
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(0); // Quantity가 0인 경우
        ItemReader<CartItem> reader = new OldCartItemReader(cartItemJdbcRepository);
        Iterator<CartItem> cartItemIterator = mock(Iterator.class);
        when(cartItemIterator.hasNext()).thenReturn(true, false);
        when(cartItemIterator.next()).thenReturn(cartItem); // Quantity가 0인 CartItem 반환

        // when
        CartItem result = reader.read();

        // then
        assertThat(result).isNull(); // null 반환 확인
    }
}
