package com.example.AutumnMall.Member.controller;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Member.domain.RefreshToken;
import com.example.AutumnMall.Member.domain.Role;
import com.example.AutumnMall.Member.dto.*;
import com.example.AutumnMall.Member.mapper.MemberMapper;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.JwtTokenizer;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.Member.service.MemberService;
import com.example.AutumnMall.Member.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/members")
public class MemberController {

    private final JwtTokenizer jwtTokenizer;
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    private final MemberMapper memberMapper;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<MemberSignupResponseDto> signup(@RequestBody @Valid MemberSignupDto memberSignupDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<MemberSignupResponseDto>(HttpStatus.BAD_REQUEST);
        }
        Member saveMember = memberService.addMember(memberSignupDto);

        MemberSignupResponseDto memberSignupResponse =
                memberMapper.memberSignupResponseDtoToMember(saveMember);
        // 회원가입
        return new ResponseEntity<>(memberSignupResponse, HttpStatus.CREATED);
    }

    // 정보 수정
    @PatchMapping("/write")
    public ResponseEntity<MemberSignupResponseDto> updateMember(@IfLogin LoginUserDto loginUserDto, @RequestBody @Valid MemberUpdateDto memberUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<MemberSignupResponseDto>(HttpStatus.BAD_REQUEST);
        }

        // 만약 로그인한 사용자와 수정하고자 하는 정보의 이메일 이 같지 않을 경우 리턴
        if(!Objects.equals(loginUserDto.getEmail(), memberUpdateDto.getEmail())){
            return new ResponseEntity<MemberSignupResponseDto>(HttpStatus.BAD_REQUEST);
        }

        // 회원 정보 수정
        Member updatedMember = memberService.updateMember(memberUpdateDto);

        // 응답 DTO 생성
        MemberSignupResponseDto memberSignupResponse = memberMapper.memberSignupResponseDtoToMember(updatedMember);

        return new ResponseEntity<>(memberSignupResponse, HttpStatus.CREATED);
    }


    // 로그인
    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponseDto> login(@RequestBody @Valid MemberLoginDto loginDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<MemberLoginResponseDto>(HttpStatus.BAD_REQUEST);
        }

        // email이 없을 경우 Exception이 발생한다. Global Exception에 대한 처리가 필요하다.
        Member member = memberService.findByEmail(loginDto.getEmail());
        if(!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())){
            return new ResponseEntity<MemberLoginResponseDto>(HttpStatus.UNAUTHORIZED);
        }
        // List<Role> ===> List<String>
        List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        // JWT토큰을 생성하였다. jwt라이브러리를 이용하여 생성.
        String accessToken = jwtTokenizer.createAccessToken(member.getMemberId(), member.getEmail(), member.getName(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.getMemberId(), member.getEmail(), member.getName(), roles);

        // RefreshToken을 DB에 저장한다. 성능 때문에 DB가 아니라 Redis에 저장하는 것이 좋다.
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setMemberId(member.getMemberId());
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        MemberLoginResponseDto loginResponse = memberMapper.toLoginResponseDtoToMember(
                member.getMemberId(),
                member.getName(),
                accessToken,
                refreshToken);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        refreshTokenService.deleteRefreshToken(refreshTokenDto.getRefreshToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
    1. 전달받은 유저의 아이디로 유저가 존재하는지 확인한다.
    2. RefreshToken이 유효한지 체크한다.
    3. AccessToken을 발급하여 기존 RefreshToken과 함께 응답한다.
     */
    @PostMapping("/refreshToken")
    public ResponseEntity<MemberLoginResponseDto> requestRefresh(@RequestBody RefreshTokenDto refreshTokenDto) {
        RefreshToken refreshToken = refreshTokenService.findRefreshToken(refreshTokenDto.getRefreshToken()).orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken.getValue());

        Long memberId = Long.valueOf((Integer)claims.get("memberId"));

        Member member = memberService.getMember(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof List<?>) {
            List<?> rolesList = (List<?>) rolesObject;
            // 안전한 형변환을 위해 제네릭 타입을 확인
            List<String> roles = rolesList.stream()
                    .filter(item -> item instanceof String)
                    .map(item -> (String) item)
                    .collect(Collectors.toList());

            String email = claims.getSubject();

            String accessToken = jwtTokenizer.createAccessToken(memberId, email, member.getName(), roles);

            MemberLoginResponseDto loginResponse = memberMapper.toLoginResponseDtoToMember(
                    member.getMemberId(),
                    member.getName(),
                    accessToken,
                    refreshTokenDto.getRefreshToken()
            );
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } else {
            // roles가 List가 아닌 경우 처리
            throw new IllegalArgumentException("Roles are not in the expected List format");
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Member> userinfo(@IfLogin LoginUserDto loginUserDto) {
        Member member = memberService.findByEmail(loginUserDto.getEmail());
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @PostMapping("/checkPassword")
    public boolean checkPassword(@IfLogin LoginUserDto loginUserDto,
                                 @RequestBody @Valid CheckPasswordDto checkPasswordDto){
        Optional<Member> member = memberService.getMember(loginUserDto.getMemberId());

        return passwordEncoder.matches(checkPasswordDto.getPassword(), member.get().getPassword());
    }

    @PatchMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordChangeDto passwordChangeDto,
                                            @IfLogin LoginUserDto loginUserDto,
                                            BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Long memberId = loginUserDto.getMemberId();
        Member member = memberService.getMember(memberId).orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));


        if(!passwordEncoder.matches(passwordChangeDto.getOldPassword(), member.getPassword())){
            return new ResponseEntity<>("기존 비밀번호가 맞지 않습니다", HttpStatus.UNAUTHORIZED);
        }

        memberService.updateMemberPassword(memberId, passwordChangeDto.getNewPassword());

        return new ResponseEntity<>("비밀번호가 성공적으로 변경되었습니다.", HttpStatus.OK);
    }
}
