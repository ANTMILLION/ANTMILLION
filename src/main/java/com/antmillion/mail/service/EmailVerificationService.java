package com.antmillion.mail.service;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.antmillion.mail.store.EmailVerificationStore;
import com.antmillion.mail.store.EmailVerificationStore.VerifyResult;

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

    private String nEmail(String email) {
        return (email == null) ? "" : email.trim().toLowerCase();
    }

    public SendResult sendCode(String email, String ip) {
        email = nEmail(email);

        if (store.isLocked(email)) {
            long sec = store.lockSecondsLeft(email);
            return SendResult.fail("요청이 잠시 차단되었습니다. " + sec + "초 후 다시 시도해 주세요.");
        }

        if (!store.allowIp(ip)) {
            long sec = store.ipWindowSecondsLeft(ip);
            return SendResult.fail("요청이 너무 많습니다. " + (sec > 0 ? (sec + "초 후") : "잠시 후") + " 다시 시도해 주세요.");
        }

        if (!store.acquireCooldown(email)) {
            long sec = store.cooldownSecondsLeft(email);
            return SendResult.fail("재전송은 " + (sec > 0 ? sec : 1) + "초 후 가능합니다.");
        }

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
        return SendResult.ok();
    }

    public ConfirmResult confirmCode(String email, String code) {
        email = nEmail(email);
        code = (code == null) ? "" : code.trim();

        VerifyResult vr = store.verifyCode(email, code);
        if (vr.isOk()) {
            return ConfirmResult.ok();
        }

        if ("LOCKED".equals(vr.getReason())) {
            return ConfirmResult.fail("인증 시도가 너무 많습니다. " + vr.getSecondsLeft() + "초 후 다시 시도해 주세요.");
        }
        if ("NO_CODE".equals(vr.getReason())) {
            return ConfirmResult.fail("인증번호가 없거나 만료되었습니다. 다시 전송해 주세요.");
        }
        if ("BAD_CODE".equals(vr.getReason())) {
            return ConfirmResult.fail("인증번호가 올바르지 않습니다. (남은 시도: " + vr.getRemainingTries() + "회)");
        }
        return ConfirmResult.fail("인증번호가 올바르지 않거나 만료되었습니다.");
    }

    public void clear(String email) {
        store.clearAll(nEmail(email));
    }

    public static class SendResult {
        private final boolean ok;
        private final String message;

        private SendResult(boolean ok, String message) {
            this.ok = ok;
            this.message = message;
        }

        public static SendResult ok() { return new SendResult(true, "인증번호를 전송했습니다."); }
        public static SendResult fail(String message) { return new SendResult(false, message); }

        public boolean isOk() { return ok; }
        public String getMessage() { return message; }
    }

    public static class ConfirmResult {
        private final boolean ok;
        private final String message;

        private ConfirmResult(boolean ok, String message) {
            this.ok = ok;
            this.message = message;
        }

        public static ConfirmResult ok() { return new ConfirmResult(true, "이메일 인증이 완료되었습니다."); }
        public static ConfirmResult fail(String message) { return new ConfirmResult(false, message); }

        public boolean isOk() { return ok; }
        public String getMessage() { return message; }
    }
}
