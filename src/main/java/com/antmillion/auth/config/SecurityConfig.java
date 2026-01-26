package com.antmillion.auth.config;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.antmillion.auth.jwt.CookieUtil;
import com.antmillion.auth.jwt.JwtAuthFilter;
import com.antmillion.auth.jwt.RtCookieAuthFilter;
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
    
	 // 정적 리소스나 웹소켓 엔드포인트를 시큐리티 검사에서 완전히 제외
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(new AntPathRequestMatcher("/ws-stomp/**"))
            .requestMatchers(new AntPathRequestMatcher("/resources/**"))
            .requestMatchers(new AntPathRequestMatcher("/favicon.ico"));
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtProvider);
    }

    @Bean
    public RtCookieAuthFilter rtCookieAuthFilter() {
        return new RtCookieAuthFilter(jwtProvider, refreshTokenStore);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(
                org.springframework.security.config.http.SessionCreationPolicy.STATELESS
            ))
            .authorizeHttpRequests(auth -> auth
            	// 로그인 불필요
//                .requestMatchers(new AntPathRequestMatcher("/resources/**")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/login"), new AntPathRequestMatcher("/signup"),
//                        new AntPathRequestMatcher("/signup/**")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/kakao/**")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/logout")).permitAll() <<<<<<< HEAD
//                .requestMatchers(new AntPathRequestMatcher("/error"), new AntPathRequestMatcher("/404"), new AntPathRequestMatcher("/500")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/" )).permitAll()
//
//                // 로그인 필요 (JSP는 RT 쿠키로 인증, API는 AT 헤더로 인증)
//                .requestMatchers(new AntPathRequestMatcher("/mypage"), new AntPathRequestMatcher("/mypage/**")).authenticated()
//                .requestMatchers(new AntPathRequestMatcher("/trade"), new AntPathRequestMatcher("/trade/**")).authenticated() =======
//                .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/404")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/500")).permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/ws-stomp/**")).permitAll()
//                // 여기는 “로그인 필요”
//                .requestMatchers(new AntPathRequestMatcher("/mypage/**")).authenticated()
//                .requestMatchers(new AntPathRequestMatcher("/trade/**")).authenticated() >>>>>>> refs/heads/develop
//                .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()

                // 이외에는 오픈
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
                    CookieUtil.deleteCookie(res, "RT");
                })
                .logoutSuccessUrl("/")
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                String uri = req.getRequestURI();
                String cpath = req.getContextPath();
                String path = (cpath != null && !cpath.isEmpty()) ? uri.substring(cpath.length()) : uri;

                // API 호출은 리다이렉트 대신 401로 응답(프론트 fetch에서 처리)
                if (path.startsWith("/api/") || path.startsWith("/auth/")) {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                res.sendRedirect(req.getContextPath() + "/login");
            }));

        // RT 쿠키로 화면 요청 인증
        http.addFilterBefore(rtCookieAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        // AT 헤더로 API 인증
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
          return http.build();
    }
}