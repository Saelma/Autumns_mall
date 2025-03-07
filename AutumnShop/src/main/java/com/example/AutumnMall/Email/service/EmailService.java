package com.example.AutumnMall.Email.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {

    private final Dotenv dotenv = Dotenv.configure().load();
    private static final long VERIFICATION_CODE_EXPIRE_MINUTES = 10;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 인증 코드 생성 및 이메일 전송
    public void sendVerificationEmail(String toEmail) throws MessagingException {
        String verificationCode = generateVerificationCode();

        // Redis에 인증 코드 저장 (10분간 유지)
        redisTemplate.opsForValue().set("EMAIL_VERIFICATION:" + toEmail, verificationCode, VERIFICATION_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 이메일 전송
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String senderEmail = dotenv.get("NAVER_MAIL_USERNAME") + "@naver.com";

        // 보내는 사람 설정
        helper.setFrom(senderEmail);
        // 받는 사람 설정
        helper.setTo(toEmail);
        // 제목 설정
        helper.setSubject("이메일 인증 코드");
        // 본문 설정
        helper.setText("인증 코드: " + verificationCode, true); // true: HTML 형식으로 전송

        // 이메일 보내기
        mailSender.send(message);
    }

    // 인증 코드 검증
    public boolean verifyEmailCode(String email, String inputCode) {
        String storedCode = redisTemplate.opsForValue().get("EMAIL_VERIFICATION:" + email);
        if (storedCode != null && storedCode.equals(inputCode)) {
            redisTemplate.delete("EMAIL_VERIFICATION:" + email); // 인증 완료 후 삭제
            return true;
        }
        return false;
    }

    // 6자리 인증 코드 생성
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    // 비밀번호 변경 시 인증 코드 검증
    public boolean verifyEmailCodeReset(String email, String inputCode) {
        String storedCode = redisTemplate.opsForValue().get("EMAIL_VERIFICATION:" + email);
        if (storedCode != null && storedCode.equals(inputCode)) {
            redisTemplate.delete("EMAIL_VERIFICATION:" + email); // 인증 완료 후 삭제

            // 비밀번호 변경 페이지에서 변경하고자 하는 이메일만 하기 위해 redis로 보안
            redisTemplate.opsForValue().set("PASSWORD_RESET:" + email, "true", 30, TimeUnit.MINUTES); // 30분 동안 유효
            redisTemplate.opsForValue().set("PASSWORD_RESET:" + inputCode, "true", 30, TimeUnit.MINUTES); // 30분 동안 유효

            return true;
        }
        return false;
    }
}
