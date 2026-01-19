package com.antmillion.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.antmillion.auth.jwt.JwtAuthFilter;
import com.antmillion.auth.jwt.JwtProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    public SecurityConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(
                org.springframework.security.config.http.SessionCreationPolicy.STATELESS
            ))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/resources/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/signup")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/signup/step2")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/signup/complete")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/logout")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/404")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/500")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                // 여기는 “로그인 필요”
                .requestMatchers(new AntPathRequestMatcher("/mypage/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/trade/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()
                // 이외에는 전부 오픈
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                String cpath = req.getContextPath();
                res.sendRedirect(cpath + "/login");
            }));
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}