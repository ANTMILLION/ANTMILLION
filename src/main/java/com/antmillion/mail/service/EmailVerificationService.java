package com.antmillion.mail.service;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.antmillion.mail.store.EmailVerificationStore;

@Service
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final EmailVerificationStore store;
    private final SecureRandom random = new SecureRandom();

    @Value("${mail.from}")
    private String from;

    public EmailVerificationService(JavaMailSender mailSender, EmailVerificationStore store) {
        this.mailSender = mailSender;
        this.store = store;
    }

    public void sendCode(String email) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        store.saveCode(email, code);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(email);
        msg.setSubject("[ANTMILLION] 이메일 인증번호");
        msg.setText(
                "안녕하세요. ANTMILLION 이메일 인증번호입니다.\n\n" +
                "인증번호: " + code + "\n\n" +
                "※ 인증번호는 일정 시간 내에만 유효합니다."
        );

        mailSender.send(msg);
    }

    public boolean confirmCode(String email, String code) {
        return store.verifyCode(email, code);
    }

    public boolean isVerified(String email) {
        return store.isVerified(email);
    }

    public void clear(String email) {
        store.clear(email);
    }
}
