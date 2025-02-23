package com.example.AutumnMall.batch.writer;

import com.example.AutumnMall.Member.domain.Mileage;
import com.example.AutumnMall.Member.repository.MileageJdbcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MileageItemWriter implements ItemWriter<Mileage> {

    private final MileageJdbcRepository mileageJdbcRepository;

    public MileageItemWriter(MileageJdbcRepository mileageJdbcRepository) {
        this.mileageJdbcRepository = mileageJdbcRepository;
    }

    @Override
    public void write(List<? extends Mileage> items) throws Exception {
        if (!items.isEmpty()) {
            // 해당 마일리지들에 대한 소멸 처리
            List<Long> mileageIds = items.stream()
                    .map(Mileage::getId)  // Mileage 객체에서 ID만 추출
                    .collect(Collectors.toList());

            // 배치에서 한 번에 여러 마일리지 소멸 처리
            mileageJdbcRepository.batchExpireMileage(mileageIds);

            // 로그 추가: 배치 마일리지 처리 완료
            log.info("배치 마일리지 소멸 처리 완료: {}건", mileageIds.size());
        }
    }
}
