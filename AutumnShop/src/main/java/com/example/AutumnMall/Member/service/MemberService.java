package com.example.AutumnMall.Member.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Member.domain.Role;
import com.example.AutumnMall.Member.dto.MemberSignupDto;
import com.example.AutumnMall.Member.dto.MemberUpdateDto;
import com.example.AutumnMall.Member.mapper.MemberMapper;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Member.repository.RoleRepository;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final MemberMapper memberMapper;

    @Transactional(readOnly = true)
    public Member findByEmail(String email){
        try {
            return memberRepository.findByEmail(email).orElseThrow(() -> {
                log.error("이메일에 맞는 해당 사용자가 없습니다. : {}", email);
                return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
            });
        } catch (BusinessLogicException e) {
            log.error("멤버 조회 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        } catch (Exception e) {
            log.error("멤버 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Member addMember(MemberSignupDto memberSignupDto) {
        try {
            Member member = memberMapper.memberSignupDtoToMember(memberSignupDto);
            // mapper를 사용하면 평문으로 저장되기에 서비스단에서 처리
            member.setPassword(passwordEncoder.encode(memberSignupDto.getPassword()));

            Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
            member.addRole(userRole.get());
            Member saveMember = memberRepository.save(member);

            // 로그 추가: 회원 가입
            log.info("새로운 회원이 등록되었습니다: {}", saveMember.getEmail());
            return saveMember;
        } catch (Exception e) {
            log.error("멤버 추가 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMember(Long memberId){
        try {
            return memberRepository.findById(memberId);
        } catch (Exception e) {
            log.error("멤버 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMember(String email){
        try{
            return memberRepository.findByEmail(email);
        } catch (Exception e) {
            log.error("멤버 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Member updateMember(MemberUpdateDto memberUpdateDto){
        try {
            // 업데이트할 멤버 검색
            Optional<Member> existingMember = memberRepository.findByEmail(memberUpdateDto.getEmail());
            Member member = existingMember.get();

            // 수정할 정보만 업데이트
            memberMapper.updateMemberFromDto(memberUpdateDto, member);

            // 로그 추가: 회원 정보 수정
            log.info("회원 {}의 정보가 수정되었습니다: {}", member.getEmail(), member);

            return memberRepository.save(member);
        } catch (Exception e) {
            log.error("멤버 업데이트 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    // mapper를 사용할 경우 평문으로 저장되므로 서비스단에서만 처리
    @Transactional
    public Member updateMemberPassword(Long memberId, String newPassword){
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", + memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });
            member.setPassword(passwordEncoder.encode(newPassword));

            // 로그 추가: 비밀번호 수정
            log.info("회원 {}의 비밀번호가 변경되었습니다.", memberId);

            return memberRepository.save(member);
        } catch (BusinessLogicException e) {
            log.error("멤버 조회 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        } catch (Exception e) {
            log.error("멤버 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }
}
