package com.antmillion.mail.controller;

import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.antmillion.mail.service.EmailVerificationService;
import com.antmillion.mail.service.EmailVerificationService.ConfirmResult;
import com.antmillion.mail.service.EmailVerificationService.SendResult;

@RestController
@RequestMapping("/signup/email")
public class EmailVerificationController {

    public static final String SESSION_VERIFIED_EMAIL = "EMAIL_VERIFIED_EMAIL";
    public static final String SESSION_VERIFIED_AT = "EMAIL_VERIFIED_AT";

    private static final Pattern EMAIL_RULE =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final EmailVerificationService service;

    public EmailVerificationController(EmailVerificationService service) {
        this.service = service;
    }

    private String nEmail(String email) {
        return (email == null) ? "" : email.trim().toLowerCase();
    }

    private String clientIp(HttpServletRequest req) {
        // 프록시 환경 고려 (첫 IP)
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int comma = xff.indexOf(',');
            return (comma > 0 ? xff.substring(0, comma) : xff).trim();
        }
        return req.getRemoteAddr();
    }

    @PostMapping("/send")
    public Map<String, Object> send(@RequestParam("email") String email,
                                    HttpServletRequest req,
                                    HttpSession session) {
        email = nEmail(email);
        if (email.isEmpty() || !EMAIL_RULE.matcher(email).matches()) {
            return Map.of("ok", false, "message", "이메일 형식이 올바르지 않습니다.");
        }

        // 새로 전송하면 기존 세션 인증상태는 무효화
        session.removeAttribute(SESSION_VERIFIED_EMAIL);
        session.removeAttribute(SESSION_VERIFIED_AT);

        String ip = clientIp(req);
        SendResult r = service.sendCode(email, ip);
        return Map.of("ok", r.isOk(), "message", r.getMessage());
    }

    @PostMapping("/confirm")
    public Map<String, Object> confirm(@RequestParam("email") String email,
                                       @RequestParam("code") String code,
                                       HttpSession session) {
        email = nEmail(email);
        code = (code == null) ? "" : code.trim();

        if (email.isEmpty() || code.isEmpty()) {
            return Map.of("ok", false, "message", "이메일/인증번호를 입력하세요.");
        }
        if (!EMAIL_RULE.matcher(email).matches()) {
            return Map.of("ok", false, "message", "이메일 형식이 올바르지 않습니다.");
        }
        if (!code.matches("^\\d{6}$")) {
            return Map.of("ok", false, "message", "인증번호 6자리를 입력하세요.");
        }

        ConfirmResult r = service.confirmCode(email, code);
        if (r.isOk()) {
            // "이 세션"에서만 유효하도록 세션에 바인딩
            session.setAttribute(SESSION_VERIFIED_EMAIL, email);
            session.setAttribute(SESSION_VERIFIED_AT, System.currentTimeMillis());
        }

        return Map.of("ok", r.isOk(), "message", r.getMessage());
    }
}
