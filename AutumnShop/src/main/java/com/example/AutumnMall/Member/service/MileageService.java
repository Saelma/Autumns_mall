package com.example.AutumnMall.Member.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Member.domain.Mileage;
import com.example.AutumnMall.Member.domain.MileageType;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Member.repository.MileageRepository;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MileageService {
    private final MileageRepository mileageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addMileage(Long memberId, int amount) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });

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

            // 로그 추가: 마일리지 적립
            log.info("회원 {}에게 마일리지가 적립되었습니다: {}원", memberId, amount);
        } catch (BusinessLogicException e) {
            log.error("마일리지 적립 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        } catch (Exception e) {
            log.error("마일리지 적립 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void minusMileage(Long memberId, int amount){
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });

            if (member.getTotalMileage() < amount) {
                throw new IllegalArgumentException("사용할 마일리지가 부족합니다.");
            }

            // 오래된 순서대로 'ADD' 타입의 적립 마일리지 가져오기
            List<Mileage> addMileages = mileageRepository.findByMemberAndTypeOrderByExpirationDateAsc(member, MileageType.ADD);

            // 사용할 남은 마일리지
            int useRemainingAmount = amount;

            // 오래된 적립 마일리지 부터 사용
            for (Mileage mileage : addMileages) {
                if (useRemainingAmount <= 0) break; // 사용할 마일리지만큼 사용했다면 종료

                if (mileage.getRemainAmount() > 0) {
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

                    // 로그 추가: 마일리지 사용
                    log.info("회원 {}가 마일리지를 사용했습니다: {}원", memberId, deduction);
                }

                if (mileage.getRemainAmount() <= 0) {
                    mileage.setType(MileageType.EXPIRATION);
                    mileage.setDescription("마일리지 적립 후 사용 완료");
                }
            }

            if (useRemainingAmount > 0) {
                throw new IllegalArgumentException("사용할 수 있는 마일리지가 부족합니다.");
            }

            updateTotalMileage(member);
        }catch(IllegalArgumentException e){
            log.error("마일리지 사용 실패 : {}", e.getMessage(), e);
            throw e;
        }catch (BusinessLogicException e) {
            log.error("마일리지 사용 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }catch (Exception e) {
            log.error("마일리지 사용 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    // 멤버 총합 마일리지 업데이트 ("ADD 기준")
    private void updateTotalMileage(Member member){
        try {
            int newTotalMileage = mileageRepository.findByMemberAndType(member, MileageType.ADD).stream()
                    .mapToInt(Mileage::getRemainAmount)
                    .sum();

            member.setTotalMileage(newTotalMileage);
            memberRepository.save(member);

            // 로그 추가: 총합 마일리지 업데이트
            log.info("회원 {}의 총합 마일리지 업데이트 완료: {}원", member.getMemberId(), newTotalMileage);
        }catch (Exception e) {
            log.error("마일리지 총합 업데이트 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void expireMileage(Long memberId) {
        try {
            LocalDate now = LocalDate.now();
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });

            // 만료된 적립 마일리지 목록
            List<Mileage> expiredMileages = mileageRepository.findByMemberAndExpirationDateBeforeAndType(
                    member, now, MileageType.ADD);

            // 만료된 마일리지 기록 추가 및 기존 마일리지 업데이트 ( 기존 적립 마일리지에 남은 마일리지가 있다면 소멸 처리 )
            for (Mileage mileage : expiredMileages) {
                if (mileage.getRemainAmount() > 0) {

                    // 기존 마일리지 소멸 처리
                    mileage.setRemainAmount(0);
                    mileage.setType(MileageType.EXPIRATION);
                    mileage.setDescription("마일리지 적립 후 소멸");

                    mileageRepository.save(mileage);    // 기존 마일리지 업데이트

                    // 로그 추가: 마일리지 소멸 처리
                    log.info("회원 {}의 마일리지가 소멸되었습니다: {}원", memberId, mileage.getAmount());
                }
            }
            updateTotalMileage(member);
        }catch(IllegalArgumentException e){
            log.error("마일리지 만료 처리 실패 : {}", e.getMessage(), e);
            throw e;
        }catch (BusinessLogicException e) {
            log.error("마일리지 만료 처리 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }catch (Exception e) {
            log.error("마일리지 만료 처리 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Page<Mileage> getMileageHistory(Long memberId, Pageable pageable){
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });
            return mileageRepository.findByMember(member, pageable);
        }catch (BusinessLogicException e) {
            log.error("마일리지 내역 조회 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }catch (Exception e) {
            log.error("마일리지 내역 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }
}
