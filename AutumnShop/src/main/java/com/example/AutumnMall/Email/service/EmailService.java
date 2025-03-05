package com.example.AutumnMall.Email.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final Dotenv dotenv = Dotenv.configure().load();

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String verificationCode) throws MessagingException {
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
}
