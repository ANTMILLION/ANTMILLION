package com.antmillion.auth.jwt;

import java.security.SecureRandom;
import java.util.Base64;

public class JwtSecreyKeyGen {
    public static void main(String[] args) {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        String base64 = Base64.getEncoder().encodeToString(bytes);
        System.out.println(base64);
    }
}