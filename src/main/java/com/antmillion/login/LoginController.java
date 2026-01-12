package com.antmillion.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class LoginController {

  // 로그인 화면
  @GetMapping("/login")
  public String loginForm() {
    return "login/login";
  }

  // 로그인 처리(임시: 성공하면 홈으로)
  @PostMapping("/login")
  public String loginSubmit() {
    return "redirect:/";
  }

  // 회원가입 1단계 화면
  @GetMapping("/signup")
  public String signupForm() {
    return "login/signup";
  }

  // 회원가입 1단계 처리 -> 2단계로
  @PostMapping("/signup")
  public String signupSubmit() {
    return "redirect:/signup/step2";
  }

  // 회원가입 2단계 화면
  @GetMapping("/signup/step2")
  public String signupStep2Form() {
    return "login/signup_step2";
  }

  // 회원가입 2단계 처리 -> 완료로
  @PostMapping("/signup/step2")
  public String signupStep2Submit() {
    return "redirect:/signup/complete";
  }

  // 회원가입 완료 화면
  @GetMapping("/signup/complete")
  public String signupComplete() {
    return "login/signup_complete";
  }
}