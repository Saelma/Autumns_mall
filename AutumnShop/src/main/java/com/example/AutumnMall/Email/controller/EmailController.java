package com.example.AutumnMall.Email.controller;

import com.example.AutumnMall.Email.service.EmailService;
import com.example.AutumnMall.Member.service.MemberService;
import com.example.AutumnMall.exception.BusinessLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;
    private final MemberService memberService;

    @Autowired
    public EmailController(EmailService emailService, MemberService memberService) {
        this.emailService = emailService;
        this.memberService = memberService;
    }

    // 이메일 인증 코드 전송
    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody Map<String, String> request) throws MessagingException {
        String email = request.get("email");

        try {
            // 이메일이 존재하는지 확인 만약 있다면 에러 처리, 없다면 커스텀 에러 처리로 전달하여 정상적 진행
            if (memberService.findByEmail(email) != null) {
                // 이메일이 이미 존재하는 경우
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 가입된 이메일입니다.");
            }
            // 해당 이메일로 가입된 멤버가 없을 경우 정상적으로 진행
        } catch (BusinessLogicException e){
            emailService.sendVerificationEmail(email);
            return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
        } catch (Exception e) {
            log.error("이메일 전송 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송에 실패했습니다.");
        }
        return null;
    }

    // 이메일 인증 코드 검증
    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean isValid = emailService.verifyEmailCode(email, code);
        return isValid ? ResponseEntity.ok("이메일 인증이 완료되었습니다.") : ResponseEntity.badRequest().body("인증 코드가 올바르지 않습니다.");
    }
}
