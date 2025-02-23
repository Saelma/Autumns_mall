package com.example.AutumnMall.batch.reader;

import com.example.AutumnMall.Member.domain.Mileage;
import com.example.AutumnMall.Member.repository.MileageJdbcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Component
public class MileageItemReader implements ItemReader<Mileage> {

    private final MileageJdbcRepository mileageJdbcRepository;
    private Iterator<Mileage> mileageIterator;

    @Autowired
    public MileageItemReader(MileageJdbcRepository mileageJdbcRepository) {
        this.mileageJdbcRepository = mileageJdbcRepository;
    }

    @PostConstruct
    public void init() {
        List<Mileage> expiredMileages = mileageJdbcRepository.findExpiredMileage();  // 만료된 마일리지 목록 조회
        log.info("만료된 마일리지 불러오는 중1: {}", expiredMileages);  // 데이터 확인용 로그
        mileageIterator = expiredMileages.iterator();
    }

    @Override
    public Mileage read() throws Exception {
        // Iterator가 없거나 더 이상 요소가 없을 때, 새롭게 데이터를 불러옴
        if (mileageIterator == null || !mileageIterator.hasNext()) {
            List<Mileage> expiredMileages = mileageJdbcRepository.findExpiredMileage();  // 만료된 마일리지 다시 조회
            if (expiredMileages.isEmpty()) {
                log.info("만료된 마일리지가 없습니다.");
                return null;
            }
            mileageIterator = expiredMileages.iterator();
        }

        // 다음 요소가 있으면 반환
        if (mileageIterator.hasNext()) {
            return mileageIterator.next();
        }

        return null;
    }
}
