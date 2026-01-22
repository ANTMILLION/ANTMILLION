package com.antmillion.user.interceptor;

import com.antmillion.auth.mapper.MemberMapper;
import com.antmillion.user.dto.UserRankResponseDTO;
import com.antmillion.user.service.AntRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserRankInterceptor implements HandlerInterceptor {
    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private AntRankService antRankService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                Long userId = Long.valueOf(auth.getPrincipal().toString());

                // 랭크 정보 계산
                UserRankResponseDTO userRank = antRankService.getUserRankInfo(userId);

                // Request에 담기 (JSP에서 ${userRank}로 사용)
                request.setAttribute("userRank", userRank);

            } catch (Exception e) {
                System.out.println("랭크 정보 조회 실패: " + e.getMessage());
            }
        }
        return true;
    }
}