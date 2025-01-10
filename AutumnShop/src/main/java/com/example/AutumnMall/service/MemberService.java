package com.example.AutumnMall.service;

import com.example.AutumnMall.domain.Member;
import com.example.AutumnMall.domain.Role;
import com.example.AutumnMall.dto.MemberSignupDto;
import com.example.AutumnMall.repository.MemberRepository;
import com.example.AutumnMall.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Member findByEmail(String email){
        return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
    }

    @Transactional
    public Member addMember(MemberSignupDto memberSignupDto) {
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
        return saveMember;
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMember(Long memberId){
        return memberRepository.findById(memberId);
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMember(String email){
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public Member updateMember(MemberSignupDto memberSignupDto){
        // 업데이트할 멤버 검색
        Optional<Member> existingMember = memberRepository.findByEmail(memberSignupDto.getEmail());
        Member member = existingMember.get();

        // 수정할 정보만 업데이트
        updateMemberWrite(member, memberSignupDto);
        return memberRepository.save(member);
    }

    private void updateMemberWrite(Member member, MemberSignupDto memberSignupDto) {
        if (memberSignupDto.getName() != null) {
            member.setName(memberSignupDto.getName());
        }
        if (memberSignupDto.getEmail() != null) {
            member.setEmail(memberSignupDto.getEmail());
        }
        if (memberSignupDto.getPassword() != null) {
            member.setPassword(passwordEncoder.encode(memberSignupDto.getPassword()));
        }
        if (memberSignupDto.getBirthYear() != null) {
            member.setBirthYear(Integer.parseInt(memberSignupDto.getBirthYear()));
        }
        if (memberSignupDto.getBirthMonth() != null) {
            member.setBirthMonth(Integer.parseInt(memberSignupDto.getBirthMonth()));
        }
        if (memberSignupDto.getBirthDay() != null) {
            member.setBirthDay(Integer.parseInt(memberSignupDto.getBirthDay()));
        }
        if (memberSignupDto.getGender() != null) {
            member.setGender(memberSignupDto.getGender());
        }
        if (memberSignupDto.getPhone() != null) {
            member.setPhone(memberSignupDto.getPhone());
        }
        if (memberSignupDto.getRoadAddress() != null) {
            member.setRoadAddress(memberSignupDto.getRoadAddress());
        }
        if (memberSignupDto.getZipCode() != null) {
            member.setZipCode(memberSignupDto.getZipCode());
        }
        if (memberSignupDto.getDetailAddress() != null) {
            member.setDetailAddress(memberSignupDto.getDetailAddress());
        }
    }
}
