package com.example.AutumnMall.Email.controller;

import com.example.AutumnMall.Email.service.EmailService;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.mail.MessagingException;
import java.util.Random;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendEmail")
    public String sendVerificationEmail(@IfLogin LoginUserDto loginUserDto) {
        String email = loginUserDto.getEmail();

        // 인증 코드 생성
        String verificationCode = generateVerificationCode();

        try {
            // 이메일 보내기
            emailService.sendVerificationEmail(email, verificationCode);
            return "인증 코드가 이메일로 전송되었습니다.";
        } catch (MessagingException e) {
            return "이메일 전송에 실패했습니다.";
        }
    }

    // 인증 코드 생성 (6자리 숫자)
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}