package com.example.AutumnMall.Member.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Member.domain.Mileage;
import com.example.AutumnMall.Member.domain.MileageType;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Member.repository.MileageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MileageService {
    private final MileageRepository mileageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addMileage(Long memberId, int amount) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        Mileage mileage = Mileage.builder()
                .member(member)
                .amount(amount)
                .type(MileageType.ADD)
                .description("마일리지 적립")
                .expirationDate(LocalDate.now().plusDays(3))
                .remainAmount(amount) // 마일리지마다 남은 마일리지를 적립하여 남은 마일리지를 기준으로 사용하도록 함
                .build();

        member.setTotalMileage(member.getTotalMileage() + amount);
        mileageRepository.save(mileage);
    }

    @Transactional
    public void minusMileage(Long memberId, int amount){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        if(member.getTotalMileage() < amount){
            throw new IllegalArgumentException("사용할 마일리지가 부족합니다.");
        }

        // 오래된 순서대로 'ADD' 타입의 적립 마일리지 가져오기
        List<Mileage> addMileages = mileageRepository.findByMemberAndTypeOrderByExpirationDateAsc(member, MileageType.ADD);

        // 사용할 남은 마일리지
        int useRemainingAmount = amount;

        // 오래된 적립 마일리지 부터 사용
        for(Mileage mileage : addMileages){
            if(useRemainingAmount <= 0) break; // 사용할 마일리지만큼 사용했다면 종료

            if(mileage.getRemainAmount() > 0 ){
                // 사용할 마일리지와 남은 적립 마일리지들 중 최솟값을 사용해 사용할 마일리지가 남은 적립 마일리지를 초과하지 않도록 함
                // 만약, 사용할 마일리지가 남은 적립 마일리지보다 크다면 다음 적립 마일리지에서 계산함
                int deduction = Math.min(mileage.getRemainAmount(), useRemainingAmount);
                mileage.setRemainAmount(mileage.getRemainAmount() - deduction);
                useRemainingAmount -= deduction;

                Mileage usedMileage = Mileage.builder()
                        .member(member)
                        .amount(-deduction)
                        .type(MileageType.MINUS)
                        .description("마일리지 사용")
                        .expirationDate(LocalDate.now())
                        .remainAmount(0) // 마일리지는 사용했기 때문에 남은 마일리지에 해당되지 않음
                        .build();
                mileageRepository.save(usedMileage);
            }

            if(mileage.getRemainAmount() <= 0) {
                mileage.setType(MileageType.EXPIRATION);
                mileage.setDescription("마일리지 적립 후 사용 완료");
            }
        }

        if(useRemainingAmount > 0) {
            throw new IllegalArgumentException("사용할 수 있는 마일리지가 부족합니다.");
        }

        updateTotalMileage(member);
    }

    // 멤버 총합 마일리지 업데이트 ("ADD 기준")
    private void updateTotalMileage(Member member){
        int newTotalMileage = mileageRepository.findByMemberAndType(member, MileageType.ADD).stream()
                .mapToInt(Mileage::getRemainAmount)
                .sum();

        member.setTotalMileage(newTotalMileage);
        memberRepository.save(member);
    }

    @Transactional
    public void expireMileage(Long memberId) {
        LocalDate now = LocalDate.now();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 만료된 적립 마일리지 목록
        List<Mileage> expiredMileages = mileageRepository.findByMemberAndExpirationDateBeforeAndType(
                member, now, MileageType.ADD);

        // 만료된 마일리지 기록 추가 및 기존 마일리지 업데이트 ( 기존 적립 마일리지에 남은 마일리지가 있다면 소멸 처리 )
        for (Mileage mileage : expiredMileages) {
            if(mileage.getRemainAmount() > 0) {

                // 기존 마일리지 소멸 처리
                mileage.setRemainAmount(0);
                mileage.setType(MileageType.EXPIRATION);
                mileage.setDescription("마일리지 적립 후 소멸");

                mileageRepository.save(mileage);    // 기존 마일리지 업데이트
            }
        }
        updateTotalMileage(member);
    }

    public Page<Mileage> getMileageHistory(Long memberId, Pageable pageable){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        return mileageRepository.findByMember(member, pageable);
    }
}
