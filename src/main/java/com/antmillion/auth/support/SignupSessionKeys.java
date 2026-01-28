package com.antmillion.auth.support;

// 회원가입/카카오 가입 플로우에서 공통으로 쓰는 세션 키 모음. (문자열 하드코딩을 줄이기)
public final class SignupSessionKeys {

	private SignupSessionKeys() {}

    // 회원가입 Step1 저장 
    public static final String SIGNUP_FORM = "signupForm";

    // 카카오 가입 진행 키(UUID)
    public static final String KAKAO_SIGNUP_KEY = "KAKAO_SIGNUP_KEY";

    // 이메일 인증 완료한 이메일
    public static final String EMAIL_VERIFIED_EMAIL = "EMAIL_VERIFIED_EMAIL";

    // 이메일 인증 완료 시각
    public static final String EMAIL_VERIFIED_AT = "EMAIL_VERIFIED_AT";
}
