package com.antmillion.myinfo.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.antmillion.auth.jwt.CookieUtil;
import com.antmillion.myinfo.dto.MyInfoView;
import com.antmillion.myinfo.service.MyInfoService;

@RequestMapping("/myinfo")
@Controller
public class MyInfoController {

    private static final Pattern PW_RULE = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    private static final DateTimeFormatter JOIN_FMT = DateTimeFormatter.ofPattern("yyyy. M. d 가입");

    private final MyInfoService myInfoService;

    public MyInfoController(MyInfoService myInfoService) {
        this.myInfoService = myInfoService;
    }

    @GetMapping
    public String myInfoPage(Model model) {
        Long userId = currentUserId();
        if (userId == null) {
            return "redirect:/login";
        }

        MyInfoView view = myInfoService.getMyInfo(userId);

        model.addAttribute("nickname", view.getNickname());
        model.addAttribute("emailDisplay", view.getEmailDisplay());
        model.addAttribute("isKakao", view.isKakao());
        model.addAttribute("joinDate", formatJoinDate(view.getJoinDate()));

        return "myinfo/myinfo";
    }

    @PostMapping("/password")
    @ResponseBody
    public Map<String, Object> changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpServletResponse response) {

    	Long userId = currentUserId();
        if (userId == null) {
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Map.of("ok", false, "message", "로그인이 필요합니다.");
        }

        currentPassword = n(currentPassword);
        newPassword = n(newPassword);
        confirmPassword = n(confirmPassword);

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            return Map.of("ok", false, "message", "모든 항목을 입력해 주세요.");
        }
        if (!newPassword.equals(confirmPassword)) {
            return Map.of("ok", false, "message", "새 비밀번호가 일치하지 않습니다.");
        }
        if (!PW_RULE.matcher(newPassword).matches()) {
            return Map.of("ok", false, "message", "비밀번호는 영문과 숫자를 포함해 8자리 이상이어야 합니다.");
        }

        try {
            myInfoService.changePassword(userId, currentPassword, newPassword);
            return Map.of("ok", true, "message", "비밀번호가 변경되었습니다.");
        } catch (IllegalStateException e) {
            return Map.of("ok", false, "message", e.getMessage());
        } catch (Exception e) {
            return Map.of("ok", false, "message", "비밀번호 변경에 실패했습니다.");
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> deleteAccount(
            @RequestParam(value = "password", required = false) String password,
            HttpServletResponse response) {

        Long userId = currentUserId();
        if (userId == null) {
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // ★ 핵심
            return Map.of("ok", false, "message", "로그인이 필요합니다.");
        }

        password = n(password);

        try {
            myInfoService.deleteAccount(userId, password);

            // RT 쿠키 제거(클라이언트는 응답 성공 후 AT(sessionStorage) 제거)
            CookieUtil.deleteCookie(response, "RT");
            SecurityContextHolder.clearContext();

            return Map.of("ok", true, "redirect", "/");
        } catch (IllegalStateException e) {
            return Map.of("ok", false, "message", e.getMessage());
        } catch (Exception e) {
            return Map.of("ok", false, "message", "회원탈퇴에 실패했습니다.");
        }
    }

    private static String n(String v) {
        return (v == null) ? "" : v.trim();
    }

    private static String formatJoinDate(LocalDateTime dt) {
        if (dt == null) return "";
        return JOIN_FMT.format(dt);
    }

    private static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        try {
            return Long.valueOf(auth.getPrincipal().toString());
        } catch (Exception e) {
            return null;
        }
    }
}
