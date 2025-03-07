package com.example.AutumnMall.Member.controller;

import com.example.AutumnMall.Email.service.EmailService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
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
    private final EmailService emailService;

    private final MemberMapper memberMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

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

        String email = loginUserDto.getEmail();
        Member member = memberService.getMember(email).orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));


        if(!passwordEncoder.matches(passwordChangeDto.getOldPassword(), member.getPassword())){
            return new ResponseEntity<>("기존 비밀번호가 맞지 않습니다", HttpStatus.UNAUTHORIZED);
        }

        memberService.updateMemberPassword(email, passwordChangeDto.getNewPassword());

        return new ResponseEntity<>("비밀번호가 성공적으로 변경되었습니다.", HttpStatus.OK);
    }

    // 비밀번호 찾기 ( 이메일 인증 )

    @PostMapping("/password/reset-request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String, String> request) throws MessagingException {
        String email = request.get("email");

        // 회원이 존재하는지 확인
        if (!memberService.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("가입되지 않은 이메일입니다.");
        }

        // 이메일 인증 코드 전송
        emailService.sendVerificationEmail(email);
        return ResponseEntity.ok("비밀번호 재설정을 위한 인증 코드가 이메일로 전송되었습니다.");
    }

    // 이메일 인증 코드 검증
    @PostMapping("/password/verify")
    public ResponseEntity<String> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean isValid = emailService.verifyEmailCodeReset(email, code);
        return isValid ? ResponseEntity.ok("이메일 인증이 완료되었습니다.") : ResponseEntity.badRequest().body("인증 코드가 올바르지 않습니다.");
    }

    @PatchMapping("/password/change")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        String inputCode = request.get("inputCode");

        // Redis에서 이메일 인증 여부 확인
        String resetAllowedEmail = redisTemplate.opsForValue().get("PASSWORD_RESET:" + email);
        String resetAllowedInputCode = redisTemplate.opsForValue().get("PASSWORD_RESET:" + inputCode);

        // (1) Redis에 인증 기록이 있는 경우 → 비밀번호 변경 가능
        if ((resetAllowedInputCode != null && resetAllowedInputCode.equals("true") &&
                (resetAllowedEmail != null && resetAllowedEmail.equals("true")))) {
            memberService.updateMemberPassword(email, newPassword);
            redisTemplate.delete("PASSWORD_RESET:" + email);
            redisTemplate.delete("PASSWORD_RESET:" + inputCode);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        }
        // 이메일 인증 X → 비밀번호 변경 불가
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호 변경 권한이 없습니다.");
    }
}
