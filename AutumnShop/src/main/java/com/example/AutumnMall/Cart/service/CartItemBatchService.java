package com.example.AutumnMall.Cart.service;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.repository.CartItemJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemBatchService {
    private final CartItemJdbcRepository cartItemJdbcRepository;

    private final JobLauncher jobLauncher;
    private final Job cartItemBatchJob;

    @Transactional
    // 대량 삽입 메서드
    public void batchInsertCartItems(List<CartItem> cartItems) {
        System.out.println("배치 삽입 시작!!");
        if (!cartItems.isEmpty()) {
            cartItemJdbcRepository.batchInsertCartItems(cartItems);
            log.info("해당 멤버 : {} 가 해당 카트 : {} 의 물품들을 배치단위로 삽입했습니다. ",
                    cartItems.get(0).getCart().getMember().getMemberId(), cartItems.get(0).getCart().getId()
            );
            System.out.println("로그 테스트: 배치 삽입 로그가 출력되었습니다.");  // 로그 확인을 위한 추가 출력
        } else {
            log.warn("배치 삽입할 카트 아이템이 없습니다.");
        }
    }

    @Transactional
    // 오래된 장바구니 아이템 삭제 (예: 30일 이상된 항목 삭제)
    public void deleteOldCartItems(int days) {
        cartItemJdbcRepository.deleteOldCartItems(days);
        log.info("30일 지난 장바구니 아이템을 삭제했습니다. ");
    }


    public boolean runCartItemBatch() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // 실행마다 다른 파라미터 제공
                    .addLong("id", 1L)
                    .toJobParameters();

            jobLauncher.run(cartItemBatchJob, jobParameters);
            return true;
        } catch (Exception e) {
            log.error("30일 지난 장바구니 아이템 삭제 배치 작업을 실패했습니다.", e);
            return false;
        }
    }
}
