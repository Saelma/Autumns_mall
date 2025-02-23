package com.example.AutumnMall.Member.repository;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Member.domain.Mileage;
import com.example.AutumnMall.Member.domain.MileageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MileageJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 회원의 마일리지 조회
     */
    public List<Mileage> findMileageByMember(Long memberId) {
        String sql = "SELECT id, member_id, amount, remain_amount, type, expiration_date " +
                "FROM mileage WHERE member_id = ? AND expiration_date < CURRENT_DATE AND type = 'ADD'";

        return jdbcTemplate.query(sql, new Object[]{memberId}, (rs, rowNum) ->
                Mileage.builder()
                        .id(rs.getLong("id"))
                        .member(Member.builder().memberId(rs.getLong("member_id")).build()) // memberId로 Member 객체 생성
                        .amount(rs.getInt("amount"))
                        .remainAmount(rs.getInt("remain_amount"))
                        .type(MileageType.valueOf(rs.getString("type")))
                        .expirationDate(rs.getDate("expiration_date").toLocalDate())
                        .build()
        );
    }

    /**
     * 배치에서 사용할 수 있도록 한 번에 여러 마일리지 만료 처리
     */
    public void batchExpireMileage(List<Long> mileageIds) {
        String sql = "UPDATE mileage SET remain_amount = 0, type = 'EXPIRATION', description = '마일리지 만료' WHERE id = ?";

        for (Long mileageId : mileageIds) {
            jdbcTemplate.update(sql, mileageId);
        }

        log.info("배치 마일리지 만료 처리 완료: {}건", mileageIds.size());
    }

    /**
     * 만료된 마일리지 목록 조회
     */
    public List<Mileage> findExpiredMileage() {
        String sql = "SELECT id, member_id, amount, remain_amount, type, expiration_date " +
                "FROM mileage WHERE expiration_date < CURRENT_DATE AND type = 'ADD'";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Member member = Member.builder()
                    .memberId(rs.getLong("member_id"))
                    .build();  // memberId로 Member 객체 생성

            return Mileage.builder()
                    .id(rs.getLong("id"))
                    .member(member)  // 생성된 Member 객체를 설정
                    .amount(rs.getInt("amount"))
                    .remainAmount(rs.getInt("remain_amount"))
                    .type(MileageType.valueOf(rs.getString("type")))
                    .expirationDate(rs.getDate("expiration_date").toLocalDate())
                    .build();
        });
    }
}