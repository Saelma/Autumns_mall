package com.example.AutumnMall.Member.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Member.domain.Role;
import com.example.AutumnMall.Member.dto.MemberSignupDto;
import com.example.AutumnMall.Member.dto.MemberUpdateDto;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Member.repository.RoleRepository;
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

    @Transactional(readOnly = true)
    public Member findByEmail(String email){
        try {
            return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
        }catch(IllegalArgumentException e){
            log.error("멤버 조회 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Member addMember(MemberSignupDto memberSignupDto) {
        try {
            Member member = new Member();
            member.setName(memberSignupDto.getName());
            member.setEmail(memberSignupDto.getEmail());
            member.setPassword(passwordEncoder.encode(memberSignupDto.getPassword()));
            member.setBirthYear(Integer.parseInt(memberSignupDto.getBirthYear()));
            member.setBirthMonth(Integer.parseInt(memberSignupDto.getBirthMonth()));
            member.setBirthDay(Integer.parseInt(memberSignupDto.getBirthDay()));
            member.setGender(memberSignupDto.getGender());
            member.setPhone(memberSignupDto.getPhone());
            member.setRoadAddress(memberSignupDto.getRoadAddress());
            member.setZipCode(memberSignupDto.getZipCode());
            member.setDetailAddress(memberSignupDto.getDetailAddress());

            Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
            member.addRole(userRole.get());
            Member saveMember = memberRepository.save(member);

            // 로그 추가: 회원 가입
            log.info("새로운 회원이 등록되었습니다: {}", saveMember.getEmail());
            return saveMember;
        }catch(Exception e){
            log.error("멤버 추가 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMember(Long memberId){
        try {
            return memberRepository.findById(memberId);
        }catch(Exception e){
            log.error("멤버 조회 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMember(String email){
        try{
            return memberRepository.findByEmail(email);
        }catch(Exception e){
            log.error("멤버 조회 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Member updateMember(MemberUpdateDto memberUpdateDto){
        try {
            // 업데이트할 멤버 검색
            Optional<Member> existingMember = memberRepository.findByEmail(memberUpdateDto.getEmail());
            Member member = existingMember.get();

            // 수정할 정보만 업데이트
            updateMemberWrite(member, memberUpdateDto);

            // 로그 추가: 회원 정보 수정
            log.info("회원 {}의 정보가 수정되었습니다: {}", member.getEmail(), member);

            return memberRepository.save(member);
        }catch(Exception e){
            log.error("멤버 업데이트 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Member updateMemberPassword(Long memberId, String newPassword){
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            member.setPassword(passwordEncoder.encode(newPassword));

            // 로그 추가: 비밀번호 수정
            log.info("회원 {}의 비밀번호가 변경되었습니다.", memberId);

            return memberRepository.save(member);
        }catch(IllegalArgumentException e){
            log.error("멤버 패스워드 업데이트 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    // 멤버의 업데이트할 정보만 갱신
    private void updateMemberWrite(Member member, MemberUpdateDto memberUpdateDto) {
        if (memberUpdateDto.getName() != null) {
            member.setName(memberUpdateDto.getName());
        }
        if (memberUpdateDto.getEmail() != null) {
            member.setEmail(memberUpdateDto.getEmail());
        }

        if (memberUpdateDto.getBirthYear() != null) {
            member.setBirthYear(Integer.parseInt(memberUpdateDto.getBirthYear()));
        }
        if (memberUpdateDto.getBirthMonth() != null) {
            member.setBirthMonth(Integer.parseInt(memberUpdateDto.getBirthMonth()));
        }
        if (memberUpdateDto.getBirthDay() != null) {
            member.setBirthDay(Integer.parseInt(memberUpdateDto.getBirthDay()));
        }
        if (memberUpdateDto.getGender() != null) {
            member.setGender(memberUpdateDto.getGender());
        }
        if (memberUpdateDto.getPhone() != null) {
            member.setPhone(memberUpdateDto.getPhone());
        }
        if (memberUpdateDto.getRoadAddress() != null) {
            member.setRoadAddress(memberUpdateDto.getRoadAddress());
        }
        if (memberUpdateDto.getZipCode() != null) {
            member.setZipCode(memberUpdateDto.getZipCode());
        }
        if (memberUpdateDto.getDetailAddress() != null) {
            member.setDetailAddress(memberUpdateDto.getDetailAddress());
        }
    }
}
