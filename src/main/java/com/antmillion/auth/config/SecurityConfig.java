package com.antmillion.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.antmillion.auth.jwt.CookieUtil;
import com.antmillion.auth.jwt.JwtAuthFilter;
import com.antmillion.auth.jwt.JwtProvider;
import com.antmillion.auth.token.RefreshTokenStore;

import io.jsonwebtoken.Claims;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;

    public SecurityConfig(JwtProvider jwtProvider, RefreshTokenStore refreshTokenStore) {
        this.jwtProvider = jwtProvider;
		this.refreshTokenStore = refreshTokenStore;
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
//                .requestMatchers(new AntPathRequestMatcher("/resources/**")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/signup")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/signup/step2")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/signup/complete")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/logout")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/404")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/500")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
//                // 여기는 “로그인 필요”
//                .requestMatchers(new AntPathRequestMatcher("/mypage/**")).authenticated()
//                .requestMatchers(new AntPathRequestMatcher("/trade/**")).authenticated()
//                .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()
                // 이외에는 전부 오픈
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                .addLogoutHandler((req, res, auth) -> {
                    String rt = CookieUtil.getCookieValue(req, "RT");
                    if (rt != null && jwtProvider.isValid(rt)) {
                        Claims claims = jwtProvider.parseClaims(rt);
                        long userId = Long.parseLong(claims.getSubject());
                        refreshTokenStore.delete(userId);
                    }
                    CookieUtil.deleteCookie(res, "AT");
                    CookieUtil.deleteCookie(res, "RT");
                })
                .logoutSuccessUrl("/")
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                res.sendRedirect(req.getContextPath() + "/login");
            }));

          http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
          return http.build();
    }
}