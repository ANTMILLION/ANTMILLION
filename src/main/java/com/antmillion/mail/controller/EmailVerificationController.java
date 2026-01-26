package com.antmillion.mail.controller;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.*;

import com.antmillion.mail.service.EmailVerificationService;

@RestController
@RequestMapping("/signup/email")
public class EmailVerificationController {

    private static final Pattern EMAIL_RULE =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final EmailVerificationService service;

    public EmailVerificationController(EmailVerificationService service) {
        this.service = service;
    }

    // [POST] /signup/email/send  {email}
    @PostMapping("/send")
    public Map<String, Object> send(@RequestParam("email") String email) {
        email = (email == null) ? "" : email.trim();
        if (email.isEmpty() || !EMAIL_RULE.matcher(email).matches()) {
            return Map.of("ok", false, "message", "이메일 형식이 올바르지 않습니다.");
        }

        service.sendCode(email);
        return Map.of("ok", true, "message", "인증번호를 전송했습니다.");
    }

    // [POST] /signup/email/confirm  {email, code}
    @PostMapping("/confirm")
    public Map<String, Object> confirm(@RequestParam("email") String email,
                                       @RequestParam("code") String code) {
        email = (email == null) ? "" : email.trim();
        code = (code == null) ? "" : code.trim();

        if (email.isEmpty() || code.isEmpty()) {
            return Map.of("ok", false, "message", "이메일/인증번호를 입력하세요.");
        }

        boolean ok = service.confirmCode(email, code);
        return ok
                ? Map.of("ok", true, "message", "이메일 인증이 완료되었습니다.")
                : Map.of("ok", false, "message", "인증번호가 올바르지 않거나 만료되었습니다.");
    }
}
